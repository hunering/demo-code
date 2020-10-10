package org.apache.flink.quickstart;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TupleTest {

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		IntStream intStream = IntStream.range(0, 10);
		List<Integer> values = intStream.boxed().collect(Collectors.toList());

		DataStream<Integer> stream = env.fromCollection(values);

		// The following code fail to execute
		// DataStream<Tuple2<String, Integer>> tupleStream = stream.map(
		// intObj -> Tuple2.of("Value", intObj)
		// );

		// either using returns
		DataStream<Tuple2<String, Integer>> tupleStream_return = stream.map(intObj -> Tuple2.of("Value", intObj))
				.returns(Types.TUPLE(Types.STRING, Types.INT));

		// or using the explicit MapFunction
		@SuppressWarnings("serial")
		DataStream<Tuple2<String, Integer>> tupleStream_MapFunction = stream
				.map(new MapFunction<Integer, Tuple2<String, Integer>>() {
					@Override
					public Tuple2<String, Integer> map(Integer value) throws Exception {
						return Tuple2.of("String", value);
					}
				});

		tupleStream_return.print();
		tupleStream_MapFunction.print();
		
		env.execute();
	}

}
