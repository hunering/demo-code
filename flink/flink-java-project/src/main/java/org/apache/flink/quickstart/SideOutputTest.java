package org.apache.flink.quickstart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class SideOutputTest {

	public static void main(String[] args) throws Exception {

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8);
		List<Integer> list = stream.collect(Collectors.toList());

		DataStream<Integer> input = env.fromCollection(list);

		final OutputTag<String> outputTag = new OutputTag<String>("side-output") {
		};

		SingleOutputStreamOperator<Integer> mainDataStream = input.process(new ProcessFunction<Integer, Integer>() {

			@Override
			public void processElement(Integer value, ProcessFunction<Integer, Integer>.Context ctx,
					Collector<Integer> out) throws Exception {
				// emit data to regular output
				out.collect(value);

				// emit data to side output
				ctx.output(outputTag, "sideout-" + String.valueOf(value));

			}
		});

		DataStream<String> sideOutputStream = mainDataStream.getSideOutput(outputTag);

		sideOutputStream.print();

		env.execute("Side output");

	}

}
