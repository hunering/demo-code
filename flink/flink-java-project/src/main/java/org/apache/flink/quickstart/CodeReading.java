package org.apache.flink.quickstart;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is class is for flink code reading.
 */
public class CodeReading {
	public static void main(String[] args) throws Exception {
		simpleMap();
	}

	public static void simpleMap() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 10000);

		DataStreamSource<Integer> source = env.fromCollection(is.boxed().collect(Collectors.toList()));

		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> obj));
		SingleOutputStreamOperator<Integer> processedStream = withTimestampsAndWatermarks.map(input -> {
			return input + 1;
		});

		processedStream.print();

		env.execute("simpleMap");
	}
}
