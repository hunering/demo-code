package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier.Context;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.Watermark;

public class WatermarkTest {

	public static void main(String[] args) throws Exception {
		testWatermarkStrategyWithHelper();
		testCustomizedWatermarkStrategy();
	}

	static void testWatermarkStrategyWithHelper() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.getConfig().setAutoWatermarkInterval(1000);
		
		IntStream intStream = IntStream.range(0, 10);
		List<Integer> values = intStream.boxed().collect(Collectors.toList());

		DataStream<Integer> stream = env.fromCollection(values);
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = stream.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofSeconds(20))
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));

		// using returns for types
		DataStream<Tuple2<String, Integer>> tupleStream_return = withTimestampsAndWatermarks.map(intObj -> Tuple2.of("Value", intObj))
				.returns(Types.TUPLE(Types.STRING, Types.INT));


		tupleStream_return.print();
		
		env.execute();
	}
	
	static void testCustomizedWatermarkStrategy() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.getConfig().setAutoWatermarkInterval(1000);
		
		IntStream intStream = IntStream.range(0, 10);
		List<Integer> values = intStream.boxed().collect(Collectors.toList());

		DataStream<Integer> stream = env.fromCollection(values);
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = stream.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forGenerator((Context context)-> new MyBoundedOutOfOrdernessGenerator())
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));

		// using returns for types
		DataStream<Tuple2<String, Integer>> tupleStream_return = withTimestampsAndWatermarks.map(intObj -> Tuple2.of("Value", intObj))
				.returns(Types.TUPLE(Types.STRING, Types.INT));


		tupleStream_return.print();
		
		env.execute();
	}
	
	 static public class MyBoundedOutOfOrdernessGenerator implements WatermarkGenerator<Integer> {

	     private final long maxOutOfOrderness = 3500; // 3.5 seconds

	     private long currentMaxTimestamp;

	     @Override
	     public void onEvent(Integer event, long eventTimestamp, WatermarkOutput output) {
	         currentMaxTimestamp = Math.max(currentMaxTimestamp, eventTimestamp);
	     }

	     @Override
	     public void onPeriodicEmit(WatermarkOutput output) {
	         // emit the watermark as current highest timestamp minus the out-of-orderness bound
	         output.emitWatermark(new Watermark(currentMaxTimestamp - maxOutOfOrderness - 1));
	     }

	 }
}
