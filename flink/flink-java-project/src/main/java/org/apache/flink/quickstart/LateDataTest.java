package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.quickstart.CustomizedWindowTest.CountFunction;
import org.apache.flink.shaded.curator4.com.google.common.collect.Streams;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class LateDataTest {

	public static void main(String[] args) throws Exception {
		// sideOutputLateTest();
		// lateDataFilterTest();
		updateLateDataTest();

	}

	public static void sideOutputLateTest() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		IntStream is = IntStream.range(1, 100000);// .of(1, 2, 3, 4, 5, 6);

		List<Integer> ss = is.boxed().collect(Collectors.toList());
		// add the late data
		ss.add(1);
		DataStreamSource<Integer> source = env.fromCollection(ss);

		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));

		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = withTimestampsAndWatermarks.keyBy(key -> key % 10);

		WindowedStream<Integer, Integer, TimeWindow> windowedStream = keyedStream.timeWindow(Time.milliseconds(10));

		final OutputTag<Integer> outputTag = new OutputTag<Integer>("late-readings") {
		};
		windowedStream.sideOutputLateData(outputTag);

		SingleOutputStreamOperator<Tuple4<Integer, Long, Long, Integer>> processedStream = windowedStream
				.process(new CountFunction());
		// retrieve the late events from the side output as a stream
		DataStream<Integer> lateStream = processedStream.getSideOutput(outputTag);

		// processedStream.print();
		lateStream.print();
		env.execute("sideOutputLateTest");
	}

	public static void lateDataFilterTest() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		IntStream is = IntStream.range(1, 100000);// .of(1, 2, 3, 4, 5, 6);

		List<Integer> ss = is.boxed().collect(Collectors.toList());
		// add the late data
		ss.add(1);
		DataStreamSource<Integer> source = env.fromCollection(ss);

		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));

		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = withTimestampsAndWatermarks.keyBy(key -> key % 10);

		final OutputTag<Integer> outputTag = new OutputTag<Integer>("late-readings") {
		};

		SingleOutputStreamOperator<Integer> processedStream = keyedStream.process(new LateReadingsFilter());
		// retrieve the late events from the side output as a stream
		DataStream<Integer> lateStream = processedStream.getSideOutput(outputTag);

		// processedStream.print();
		lateStream.print();
		env.execute("lateDataFilterTest");
	}

	public static class LateReadingsFilter extends ProcessFunction<Integer, Integer> {

		final OutputTag<Integer> lateReadingsOut = new OutputTag<Integer>("late-readings") {
		};

		@Override
		public void processElement(Integer value, ProcessFunction<Integer, Integer>.Context ctx, Collector<Integer> out)
				throws Exception {
			// compare record timestamp with current watermark
			if (value < ctx.timerService().currentWatermark()) {
				// this is a late reading => redirect it to the side output
				ctx.output(lateReadingsOut, value);
			} else {
				out.collect(value);
			}

		}
	}

	public static void updateLateDataTest() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		IntStream is = IntStream.range(1, 100000);// .of(1, 2, 3, 4, 5, 6);

		List<Integer> ss = is.boxed().collect(Collectors.toList());
		// add the late data
		ss.add(1);

		is = IntStream.range(100000, 200000);
		ss.addAll(is.boxed().collect(Collectors.toList()));

		DataStreamSource<Integer> source = env.fromCollection(ss);

		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));

		// 1, 11, 21, 31....will be grouped into same key
		SingleOutputStreamOperator<Tuple4<Integer, Long, Long, String>> stream = withTimestampsAndWatermarks
				.keyBy(key -> key % 10)
				.timeWindow(Time.milliseconds(10))
				.allowedLateness(Time.seconds(5000))
				.process(new UpdatingWindowCountFunction());

		stream.print();
		env.execute("updateLateDataTest");
	}

	public static class UpdatingWindowCountFunction
			extends ProcessWindowFunction<Integer, Tuple4<Integer, Long, Long, String>, Integer, TimeWindow> {

		@Override
		public void process(Integer key,
				ProcessWindowFunction<Integer, Tuple4<Integer, Long, Long, String>, Integer, TimeWindow>.Context context,
				Iterable<Integer> elements, Collector<Tuple4<Integer, Long, Long, String>> out) throws Exception {
			// count the number of readings
			long cnt = Streams.stream(elements).count();

			// state to check if this is the first evaluation of the window or not
			ValueState<Boolean> isUpdate = context.windowState()
					.getState(new ValueStateDescriptor<>("lastTimer", Types.BOOLEAN));
			if (isUpdate.value() == null || !isUpdate.value()) {
				// first evaluation, emit first result
				// out.collect(Tuple4.of(key, context.window().getEnd(), cnt, "first"));
				isUpdate.update(true);
			} else {
				// not the first evaluation, emit an update
				out.collect(Tuple4.of(key, context.window().getEnd(), cnt, "update"));
			}

		}

	}
}
