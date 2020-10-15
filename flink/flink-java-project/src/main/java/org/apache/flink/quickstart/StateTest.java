package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class StateTest {

	public static void main(String[] args) throws Exception {
		// testListCheckPointed();
		testCheckpointedFunction();

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
				.map(new MyFunction<Integer>());

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

}
