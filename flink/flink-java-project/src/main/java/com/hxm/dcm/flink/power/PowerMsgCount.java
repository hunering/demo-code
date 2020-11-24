package com.hxm.dcm.flink.power;

import com.hxm.dcm.common.msg.kafka.KafkaProperty;
import com.hxm.dcm.common.msg.kafka.PowerMsg;
import com.hxm.dcm.common.msg.kafka.PowerMsgUtils;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PowerMsgCount {
	private static final String kafkaClientGroupId = "Flink_Power_Msg_Count_Client";
	private static final long boundedOutOfOrdernessSeconds = 20;
	private static final long windowLengthSeconds = 5;

	public static void main(String[] args) throws Exception {
		PowerMsgCount powerCount = new PowerMsgCount();
		StreamExecutionEnvironment env = powerCount.getExecEnv();
		powerCount.assembleFlow(env);
		env.execute();
	}

	private StreamExecutionEnvironment getExecEnv() {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.getConfig().setAutoWatermarkInterval(500);
		env.setParallelism(1);
		return env;
	}

	private void assembleFlow(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KafkaProperty.BootstrapServers);
		properties.setProperty("group.id", kafkaClientGroupId);

		FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>(KafkaProperty.PowerTopicName, new SimpleStringSchema(), properties);
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
		SingleOutputStreamOperator<Tuple2<Long, Long>> soo = windowedStream.aggregate(new PowerMsgCountAcc(), new MsgCountWindowFunction());
		soo.print();
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
