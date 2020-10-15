package org.apache.flink.quickstart;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.quickstart.CustomizedWindowTest.CountFunction;
import org.apache.flink.quickstart.CustomizedWindowTest.OneSecondIntervalTriger;
import org.apache.flink.quickstart.CustomizedWindowTest.ThreeSecondsWindowAssigner;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class JoinTest {

	public static void main(String[] args) throws Exception {
		//testTumbelingWindownJoin();
		testInternalJoin();

	}
	
	public static void testTumbelingWindownJoin() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		// make sure watermark updated as quickly as possible
		env.getConfig().setAutoWatermarkInterval(1L);
		
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 6000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source1 = env.fromCollection(is.boxed().collect(Collectors.toList()));
		
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks1 = source1.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forMonotonousTimestamps()
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
	
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks2 = source1.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forMonotonousTimestamps()
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
		
		// 1, 11, 21, 31....will be grouped into same key
		DataStream<String> outputStream = withTimestampsAndWatermarks1.join(withTimestampsAndWatermarks2)
	    .where(key -> key%10)
	    .equalTo(key -> key%10)
	    .window(TumblingEventTimeWindows.of(Time.milliseconds(20)))
	    .apply (new JoinFunction<Integer, Integer, String> (){
	        @Override
	        public String join(Integer first, Integer second) {
	            return first + "," + second;
	        }
	    });	

		
		 outputStream.print();
		 
		 env.execute("testTumbelingWindownJoin");
	}
	
	public static void testInternalJoin() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		// make sure watermark updated as quickly as possible
		env.getConfig().setAutoWatermarkInterval(1L);
		
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 6000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source1 = env.fromCollection(is.boxed().collect(Collectors.toList()));
		
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks1 = source1.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forMonotonousTimestamps()
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
	
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks2 = source1.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forMonotonousTimestamps()
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
		
		// 1, 11, 21, 31....will be grouped into same key
		DataStream<String> outputStream = withTimestampsAndWatermarks1
	    .keyBy(key -> key%10)
	    .intervalJoin(withTimestampsAndWatermarks2.keyBy(key -> key%10))
	    .between(Time.milliseconds(-2), Time.milliseconds(1))
	    .process (new ProcessJoinFunction<Integer, Integer, String>(){
			@Override
			public void processElement(Integer left, Integer right,
					ProcessJoinFunction<Integer, Integer, String>.Context ctx, Collector<String> out) throws Exception {
				out.collect(left + "," + right);				
			}
	    });
		
		 outputStream.print();
		 
		 env.execute("testInternalJoin");
	}
	
}
