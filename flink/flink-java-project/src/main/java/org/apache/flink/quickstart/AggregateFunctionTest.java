package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.quickstart.KeyedProcessFunctionTest.MyKeyedProcessFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AggregateApplyWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class AggregateFunctionTest {

	public static void main(String[] args) throws Exception {
		//reduceTest();
		//aggregateFunctionTest();
		processWindowFunctionTest();
	}

	public static void reduceTest() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 10000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source = env.fromCollection(is.boxed().collect(Collectors.toList()));
		
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
		
		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = withTimestampsAndWatermarks.keyBy(key -> key%10);
		
		 WindowedStream<Integer, Integer, TimeWindow> windowedStream = keyedStream.window(TumblingEventTimeWindows.of(Time.milliseconds(100)));

		SingleOutputStreamOperator<Integer> processedStream = windowedStream.reduce((v1, v2)->{
			if(v1 > v2) {
				return v1;
			} else {
				return v2;
			}
		});

		processedStream.print();
		
		env.execute("reduceTest");
	}
	
	public static void aggregateFunctionTest() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 10000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source = env.fromCollection(is.boxed().collect(Collectors.toList()));
		
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
		
		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = withTimestampsAndWatermarks.keyBy(key -> key%10);
		
		 WindowedStream<Integer, Integer, TimeWindow> windowedStream = keyedStream.window(TumblingEventTimeWindows.of(Time.milliseconds(100)));

		SingleOutputStreamOperator<Float> processedStream = windowedStream.aggregate(new MyAggregateFunction());

		processedStream.print();
		
		env.execute("aggregateFunctionTest");
	}
	
	public static class MyAccumulator {
		public int max;
		public int min;
	}
	
	public static class MyAggregateFunction implements AggregateFunction<Integer, MyAccumulator, Float> {

		@Override
		public MyAccumulator createAccumulator() {			
			return new MyAccumulator();
		}

		@Override
		public MyAccumulator add(Integer value, MyAccumulator accumulator) {
			if(value > accumulator.max) {
				accumulator.max = value;
			} else if (value < accumulator.min) {
				accumulator.min = value;
			}
			return accumulator;
		}

		@Override
		public Float getResult(MyAccumulator accumulator) {
			return ((float)accumulator.max + (float)accumulator.min)/2;
		}

		@Override
		public MyAccumulator merge(MyAccumulator a, MyAccumulator b) {
			MyAccumulator newAcc = new MyAccumulator();
			newAcc.max = Integer.max(a.max, b.max);
			newAcc.min = Integer.max(a.min, b.min);
			return newAcc;
		}		
	}
	
	
	public static void processWindowFunctionTest() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 10000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source = env.fromCollection(is.boxed().collect(Collectors.toList()));
		
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
		
		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = withTimestampsAndWatermarks.keyBy(key -> key%10);
		
		 WindowedStream<Integer, Integer, TimeWindow> windowedStream = keyedStream.window(TumblingEventTimeWindows.of(Time.milliseconds(100)));

		SingleOutputStreamOperator<String> processedStream = windowedStream.reduce((v1, v2)->{
			if(v1 > v2) {
				return v1;
			} else {
				return v2;
			}
		}, new MyProcessWindowFunction());

		processedStream.print();
		
		env.execute("aggregateFunctionTest");
	}
	
	public static class MyProcessWindowFunction extends ProcessWindowFunction<Integer, String, Integer, TimeWindow> {
		@Override
		public void process(Integer key, ProcessWindowFunction<Integer, String, Integer, TimeWindow>.Context context,
				Iterable<Integer> elements, Collector<String> out) throws Exception {
			 int maxValue = elements.iterator().next();
			 out.collect("Value: " + maxValue + ", window start:" + context.window().getStart() + ", window start:" + context.window().getEnd());			
		}		
	}
}
