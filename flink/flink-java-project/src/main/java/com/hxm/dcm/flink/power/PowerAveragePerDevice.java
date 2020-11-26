package com.hxm.dcm.flink.power;

import com.hxm.dcm.common.msg.influxdb.InfluxdbProperty;
import com.hxm.dcm.common.msg.kafka.KafkaProperty;
import com.hxm.dcm.common.msg.kafka.PowerMsg;
import com.hxm.dcm.common.msg.kafka.PowerMsgUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBConfig;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBPoint;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class PowerAveragePerDevice {
	private static final String kafkaClientGroupId = "Flink_Power_Average_Per_Device_Client";
	private static final long boundedOutOfOrdernessSeconds = 1;
	private static final long windowLengthSeconds = 5;

	public static void main(String[] args) throws Exception {
		PowerAveragePerDevice util = new PowerAveragePerDevice();
		StreamExecutionEnvironment env = util.getExecEnv();
		util.assembleFlow(env, KafkaProperty.BootstrapServers, KafkaProperty.PowerTopicName,
				boundedOutOfOrdernessSeconds, windowLengthSeconds);
		env.execute("Power Msg Count");
	}

	private StreamExecutionEnvironment getExecEnv() {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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

		TypeInformation<Tuple2<Long, Float>> accType = TypeInformation.of(new TypeHint<Tuple2<Long, Float>>(){});
		TypeInformation<Tuple2<Long, Float>> aggregateResultType = TypeInformation.of(new TypeHint<Tuple2<Long, Float>>(){});
		// Tuple3.of(EpochMillSeconds, DeviceKey, AveragePowerPerWindow)
		TypeInformation<Tuple3<Long, String, Float>> resultType = TypeInformation.of(new TypeHint<Tuple3<Long, String, Float>>(){});
		SingleOutputStreamOperator<Tuple3<Long, String, Float>> aggStream = withTimestampsAndWatermarks
				.keyBy(PowerMsg::getDeviceId)
				.window(TumblingEventTimeWindows.of(Time.seconds(windowLengthSeconds)))
				.aggregate(new PowerValueAcc(), new PowerAverageWindowFunction(), accType, aggregateResultType, resultType);

		DataStream<String> fileStream = aggStream.map(new RichMapFunction<Tuple3<Long, String, Float>, String>() {
			@Override
			public String map(Tuple3<Long, String, Float> value) throws Exception {
				Instant instant = Instant.ofEpochMilli(value.f0);
				LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
				return date.toString() + ": " + value.f1 + ": " + value.f2;
			}
		});

		String fileFolder = "file:///home/huxiaomi/work/demo-code/flink/flink-java-project/sink_files/dcm_power/average/";
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

		DataStream<InfluxDBPoint> dataStream = aggStream.map(
				new RichMapFunction<Tuple3<Long, String, Float>, InfluxDBPoint>() {
					@Override
					public InfluxDBPoint map(Tuple3<Long, String, Float> value) throws Exception {
						String measurement = InfluxdbProperty.PowerAverageMeasureName + value.f1;
						long timestamp = value.f0;
						HashMap<String, String> tags = new HashMap<>();
						tags.put("tag", "tag");
						HashMap<String, Object> fields = new HashMap<>();
						fields.put(InfluxdbProperty.PowerMsgCountValueField, value.f2);
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

	private static class PowerAverageWindowFunction
			extends ProcessWindowFunction<Tuple2<Long, Float>, Tuple3<Long, String, Float>, String, TimeWindow> {
		@Override
		public void process(String key, Context context, Iterable<Tuple2<Long, Float>> elements, Collector<Tuple3<Long, String, Float>> out) throws Exception {
			Tuple2<Long, Float> msgCountAndPowerAcc = elements.iterator().next();
			// Tuple3.of(EpochMillSeconds, DeviceKey, AveragePowerPerWindow)
			out.collect(Tuple3.of(context.window().getStart(), key, msgCountAndPowerAcc.f1/msgCountAndPowerAcc.f0));
		}
	}

	private static class PowerValueAcc
			implements AggregateFunction<PowerMsg, Tuple2<Long, Float>, Tuple2<Long, Float>> {
		@Override
		public Tuple2<Long, Float> createAccumulator() {
			return Tuple2.of(0L, 0f);
		}

		@Override
		public Tuple2<Long, Float> add(PowerMsg value, Tuple2<Long, Float> accumulator) {
			return Tuple2.of(accumulator.f0+1, accumulator.f1 + value.getValue());
		}

		@Override
		public Tuple2<Long, Float> getResult(Tuple2<Long, Float> accumulator) {
			return accumulator;
		}

		@Override
		public Tuple2<Long, Float> merge(Tuple2<Long, Float> a, Tuple2<Long, Float> b) {
			return Tuple2.of(a.f0 + b.f0, a.f1 + b.f1);
		}
	}
}
