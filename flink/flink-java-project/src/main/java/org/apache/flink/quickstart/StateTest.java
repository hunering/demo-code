package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.ListTypeInfo;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class StateTest {

	public static void main(String[] args) throws Exception {
		// testListCheckPointed();
		//testCheckpointedFunction();
		testKeyedBroadcastStream();

	}

	public static void testListCheckPointed() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		IntStream is = IntStream.range(1, 105);// .of(1, 2, 3, 4, 5, 6);

		List<Integer> ss = is.boxed().collect(Collectors.toList());
		DataStreamSource<Integer> source = env.fromCollection(ss);

		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));

		SingleOutputStreamOperator<Tuple2<Integer, Long>> sos = withTimestampsAndWatermarks
				.flatMap(new HighTempCounter(100));

		sos.print();

		env.execute("testListCheckPointed");

	}

	public static class HighTempCounter extends RichFlatMapFunction<Integer, Tuple2<Integer, Long>>
			implements ListCheckpointed<Long> {

		// local count variable
		private long highTempCnt = 0L;
		private int threshold;

		public HighTempCounter(int threshold) {
			this.threshold = threshold;
		}

		@Override
		public List<Long> snapshotState(long checkpointId, long timestamp) throws Exception {
			return java.util.Collections.singletonList(highTempCnt);
		}

		@Override
		public void restoreState(List<Long> state) throws Exception {
			highTempCnt = 0;
			// restore state by adding all longs of the list
			for (long cnt : state) {
				highTempCnt += cnt;
			}
		}

		@Override
		public void flatMap(Integer value, Collector<Tuple2<Integer, Long>> out) throws Exception {
			int subtaskIdx = getRuntimeContext().getIndexOfThisSubtask();
			if (value > threshold) {
				// increment counter if threshold is exceeded
				highTempCnt += 1;
				// emit update with subtask index and counter
				out.collect(Tuple2.of(subtaskIdx, highTempCnt));
			}

		}

	}

	public static void testCheckpointedFunction() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		IntStream is = IntStream.range(1, 105);// .of(1, 2, 3, 4, 5, 6);

		List<Integer> ss = is.boxed().collect(Collectors.toList());
		DataStreamSource<Integer> source = env.fromCollection(ss);

		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));

		SingleOutputStreamOperator<Integer> sos = withTimestampsAndWatermarks
				.keyBy(value -> value % 10)
				.map(new MyFunction<Integer>()).setMaxParallelism(8);

		sos.print();

		env.execute("testCheckpointedFunction");

	}

	public static class AddFunction implements ReduceFunction<Integer> {

		@Override
		public Integer reduce(Integer value1, Integer value2) throws Exception {
			return value1 + value2;
		}

	}

	public static class MyFunction<T> implements MapFunction<T, T>, CheckpointedFunction {

		private ReducingState<Integer> countPerKey;
		private ListState<Integer> countPerPartition;

		private int localCount;

		public void initializeState(FunctionInitializationContext context) throws Exception {
			// get the state data structure for the per-key state
			countPerKey = context.getKeyedStateStore().getReducingState(
					new ReducingStateDescriptor<>("perKeyCount", new AddFunction(), Integer.class));

			// get the state data structure for the per-partition state
			countPerPartition = context.getOperatorStateStore().getListState(
					new ListStateDescriptor<>("perPartitionCount", Integer.class));

			// initialize the "local count variable" based on the operator state
			for (Integer l : countPerPartition.get()) {
				localCount += l;
			}
		}

		public void snapshotState(FunctionSnapshotContext context) throws Exception {
			// the keyed state is always up to date anyways
			// just bring the per-partition state in shape
			countPerPartition.clear();
			countPerPartition.add(localCount);
		}

		public T map(T value) throws Exception {
			// update the states
			countPerKey.add(1);
			localCount++;

			return value;
		}
	}

	public static class Rule {
		private String name;
		private int threshold;

		public Rule(String name, int threshold) {
			this.name = name;
			this.threshold = threshold;
		}

		public int getThreshold() {
			return threshold;
		}

		public void setThreshold(int threshold) {
			this.threshold = threshold;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static void testKeyedBroadcastStream() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(10);
		env.getConfig().setAutoWatermarkInterval(1L);
		IntStream is = IntStream.range(1, 10000);

		List<Integer> ss = is.boxed().collect(Collectors.toList());
		DataStreamSource<Integer> source = env.fromCollection(ss);

		SingleOutputStreamOperator<Integer> sourceWithTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));

		KeyedStream<Integer, Integer> partitionedStream = sourceWithTimestampsAndWatermarks
				.keyBy(value -> value % 10);

		Stream<Rule> rules = Stream.of(new Rule("1", 1), new Rule("2", 100), new Rule("3", 10000), new Rule("4", 9000));
		DataStreamSource<Rule> ruleStream = env.fromCollection(rules.collect(Collectors.toList()));
//		SingleOutputStreamOperator<Rule> rulesWithWatermark = rules
//				.assignTimestampsAndWatermarks(WatermarkStrategy.<Rule>forBoundedOutOfOrderness(Duration.ofMillis(1))
//						.withTimestampAssigner((Rule obj, long recordTimestamp) -> { return obj.getTimestamp(); }));

		// a map descriptor to store the name of the rule (string) and the rule itself.
		MapStateDescriptor<String, Rule> ruleStateDescriptor = new MapStateDescriptor<>(
				"RulesBroadcastState",
				BasicTypeInfo.STRING_TYPE_INFO,
				TypeInformation.of(new TypeHint<Rule>() {
				}));

		// broadcast the rules and create the broadcast state
		BroadcastStream<Rule> ruleBroadcastStream = ruleStream.broadcast(ruleStateDescriptor);

		BroadcastConnectedStream<Integer, Rule> output = partitionedStream.connect(ruleBroadcastStream);
		SingleOutputStreamOperator<String> sos = output.process(new MyKeyedBroadcastProcessFunction());

		sos.print();

		env.execute("testKeyedBroadcastStream");

	}

	public static class MyKeyedBroadcastProcessFunction
			extends KeyedBroadcastProcessFunction<Integer, Integer, Rule, String> {
		// we keep a list as we may have many first elements waiting
		private final MapStateDescriptor<String, List<Integer>> mapStateDesc = new MapStateDescriptor<>(
				"items",
				BasicTypeInfo.STRING_TYPE_INFO,
				new ListTypeInfo<>(Integer.class));

		// identical to our ruleStateDescriptor above
		private final MapStateDescriptor<String, Rule> ruleStateDescriptor = new MapStateDescriptor<>(
				"RulesBroadcastState",
				BasicTypeInfo.STRING_TYPE_INFO,
				TypeInformation.of(new TypeHint<Rule>() {
				}));

		@Override
		public void processBroadcastElement(Rule value,
				KeyedBroadcastProcessFunction<Integer, Integer, Rule, String>.Context ctx, Collector<String> out)
				throws Exception {
			ctx.getBroadcastState(ruleStateDescriptor).put(value.name, value);
		}

		@Override
		public void processElement(Integer value,
				KeyedBroadcastProcessFunction<Integer, Integer, Rule, String>.ReadOnlyContext ctx,
				Collector<String> out) throws Exception {
			final MapState<String, List<Integer>> state = getRuntimeContext().getMapState(mapStateDesc);

			for (Entry<String, Rule> entry : ctx.getBroadcastState(ruleStateDescriptor).immutableEntries()) {
				final String ruleName = entry.getKey();
				final Rule rule = entry.getValue();

				List<Integer> stored = state.get(ruleName);
				if (stored == null) {
					stored = new ArrayList<>();
				}

				// when two adjacent value larger than threshold, will trigger a MATCH event
				if (value >= rule.threshold && !stored.isEmpty()) {
					String result = "rule:" + rule.getName() + ", MATCH: ";
					for (Integer i : stored) {
						result = result + i + " - ";
					}
					result = result + value;					
					out.collect(result) ;
					stored.clear();
				}

				// there is no else{} to cover if rule.first == rule.second
				if (value > rule.threshold) {
					stored.add(value);
				}

				if (stored.isEmpty()) {
					state.remove(ruleName);
				} else {
					state.put(ruleName, stored);
				}
			}

		}

	}
}
