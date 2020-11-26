package com.hxm.dcm.flink.power;

import com.hxm.dcm.common.msg.influxdb.InfluxdbProperty;
import com.hxm.dcm.common.msg.kafka.KafkaProperty;
import com.hxm.dcm.common.msg.kafka.PowerMsg;
import com.hxm.dcm.common.msg.kafka.PowerMsgUtils;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBConfig;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBPoint;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PowerMsgCount {
	private static final String kafkaClientGroupId = "Flink_Power_Msg_Count_Client";
	private static final long boundedOutOfOrdernessSeconds = 1;
	private static final long windowLengthSeconds = 5;

	transient Logger LOG = LoggerFactory.getLogger(PowerMsgCount.class);

	public static void main(String[] args) throws Exception {
		PowerMsgCount powerCount = new PowerMsgCount();
		StreamExecutionEnvironment env = powerCount.getExecEnv();
		powerCount.assembleFlow(env, KafkaProperty.BootstrapServers, KafkaProperty.PowerTopicName,
				boundedOutOfOrdernessSeconds, windowLengthSeconds);
		env.execute("Power Msg Count");
	}

	private StreamExecutionEnvironment getExecEnv() {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		//String cwd = Paths.get("/home/huxiaomi/tools/flink-1.11.2/conf").toAbsolutePath().normalize().toString();
		//Configuration conf = GlobalConfiguration.loadConfiguration(cwd);
		//StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.getConfig().setAutoWatermarkInterval(500);
		env.setParallelism(1);
		return env;
	}

	private void assembleFlow(StreamExecutionEnvironment env, String kafkaServers, String topicName,
							  long boundedOutOfOrdernessSeconds, long windowLengthSeconds) {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", kafkaServers);
		properties.setProperty("group.id", kafkaClientGroupId);

		FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), properties);
		kafkaConsumer.setStartFromEarliest();
		DataStream<String> stream = env.addSource(kafkaConsumer);

		DataStream<PowerMsg> powerMsgStream = stream.flatMap(new FlatMapFunction<String, PowerMsg>() {
			@Override
			public void flatMap(String value, Collector<PowerMsg> out) throws Exception {
				try {
					PowerMsg msg = PowerMsgUtils.Json2Msg(value);
					out.collect(msg);

				} catch (Exception ignored) {

				}
			}
		}).name("Filter-out-invalid-msg");

		SingleOutputStreamOperator<PowerMsg> withTimestampsAndWatermarks = powerMsgStream.assignTimestampsAndWatermarks(
				WatermarkStrategy.<PowerMsg>forBoundedOutOfOrderness(Duration.ofSeconds(boundedOutOfOrdernessSeconds))
						.withTimestampAssigner((obj, recordTimestamp) -> {
							return obj.getTimeStamp().toInstant().toEpochMilli();
						})).name("watermarked-power-msg");

		AllWindowedStream<PowerMsg, TimeWindow> windowedStream = withTimestampsAndWatermarks.windowAll(TumblingEventTimeWindows.of(Time.seconds(windowLengthSeconds)));
		SingleOutputStreamOperator<Tuple2<Long, Long>> countStream = windowedStream.aggregate(new PowerMsgCountAcc(), new MsgCountWindowFunction());

		DataStream<String> fileStream = countStream.map(new RichMapFunction<Tuple2<Long, Long>, String>() {
			@Override
			public String map(Tuple2<Long, Long> value) throws Exception {
				Instant instant = Instant.ofEpochMilli(value.f0);
				LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
				return date.toString() + ": " +value.f1;
			}
		});

		String fileFolder = "file:///home/huxiaomi/work/demo-code/flink/flink-java-project/sink_files/dcm_power/msg_count/";
		StreamingFileSink<String> fileSink = StreamingFileSink
				.forRowFormat(new org.apache.flink.core.fs.Path(fileFolder), new SimpleStringEncoder<String>("UTF-8"))
				.withRollingPolicy(
						DefaultRollingPolicy.builder()
								.withRolloverInterval(TimeUnit.SECONDS.toMillis(15))
								.withInactivityInterval(TimeUnit.SECONDS.toMillis(5))
								.withMaxPartSize(1024 * 1024 * 1024)
								.build())
				.build();

		fileStream.addSink(fileSink);

		DataStream<InfluxDBPoint> dataStream = countStream.map(
				new RichMapFunction<Tuple2<Long, Long>, InfluxDBPoint>() {
					@Override
					public InfluxDBPoint map(Tuple2<Long, Long> value) throws Exception {
						String measurement = InfluxdbProperty.PowerMeasurementName;
						long timestamp = value.f0;
						HashMap<String, String> tags = new HashMap<>();
						tags.put("tag", "tag");
						HashMap<String, Object> fields = new HashMap<>();
						fields.put(InfluxdbProperty.PowerValueField, value.f1);
						return new InfluxDBPoint(measurement, timestamp, tags, fields);
					}
				}
		);

		InfluxDBConfig influxDBConfig = InfluxDBConfig.builder(InfluxdbProperty.influxdbServers,
				InfluxdbProperty.username, InfluxdbProperty.password, InfluxdbProperty.dbName)
				.batchActions(1000)
				.flushDuration(100, TimeUnit.MILLISECONDS)
				.enableGzip(true)
				.build();

		dataStream.addSink(new InfluxDBSink(influxDBConfig));

		dataStream.print();
	}

	private static class MsgCountWindowFunction
			extends ProcessAllWindowFunction<Long, Tuple2<Long, Long>, TimeWindow> {
		@Override
		public void process(Context context, Iterable<Long> elements, Collector<Tuple2<Long, Long>> out) throws Exception {
			Long msgCount = elements.iterator().next();
			out.collect(new Tuple2<Long, Long>(context.window().getStart(), msgCount));
		}
	}

	private static class PowerMsgCountAcc
			implements AggregateFunction<PowerMsg, Long, Long> {

		@Override
		public Long createAccumulator() {
			return 0L;
		}

		@Override
		public Long add(PowerMsg value, Long accumulator) {
			return accumulator + 1;
		}

		@Override
		public Long getResult(Long accumulator) {
			return accumulator;
		}

		@Override
		public Long merge(Long a, Long b) {
			return a + b;
		}
	}
}
