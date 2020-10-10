package org.apache.flink.quickstart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.runtime.plugable.SerializationDelegate;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.runtime.partitioner.StreamPartitioner;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class PartitionTest {

	public static void main(String[] args) throws Exception {

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		IntStream stream = IntStream.range(1, 25);
		List<Integer> list = stream.boxed().collect(Collectors.toList());

		DataStream<Integer> input = env.fromCollection(list);

		MyPartitioner partitioner = new MyPartitioner();
		DataStream<Integer> partioned = input.partitionCustom(partitioner, obj -> obj);

		DataStream<Integer> mapped = partioned.map(new RichMapFunction<Integer, Integer>() {
			@Override
			public Integer map(Integer value) throws Exception {
				int i = getRuntimeContext().getIndexOfThisSubtask();
				System.out.println("value: " + value + ", Index: " + i);
				return value;
			}
		});

		mapped.print();
		env.execute("Side output");

	}

	public static class MyPartitioner implements Partitioner<Integer> {
		@Override
		public int partition(Integer key, int numPartitions) {
			int p = key % numPartitions;
			System.out.println("Key: " + key + "; p: " + p + ", numPartitions: " + numPartitions);
			return p;
		}
	}

}
