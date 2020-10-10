package org.apache.flink.quickstart;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction.OnTimerContext;
import org.apache.flink.util.Collector;

public class KeyedProcessFunctionTest {

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 10000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source = env.fromCollection(is.boxed().collect(Collectors.toList()));
		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = source.keyBy(key -> key%10);

		SingleOutputStreamOperator<Long> processedStream = keyedStream.process(new MyKeyedProcessFunction());

		processedStream.print();
		
		env.execute("KeyedProcessFunctionTest");
	}

	public static class MyKeyedProcessFunction extends KeyedProcessFunction<Integer, Integer, Long> {

		private ValueState<Integer> lastTemp;
		private ValueState<Long> currentTimer;
		private ValueState<Long> counter;
		@Override
		public void open(Configuration parameters) throws Exception {
			lastTemp = getRuntimeContext().getState(new ValueStateDescriptor<>("lastTemperature", Types.INT));
			currentTimer = getRuntimeContext().getState(new ValueStateDescriptor<>("lastTimer", Types.LONG));
			counter = getRuntimeContext().getState(new ValueStateDescriptor<>("counter", Types.LONG, 1l));
		}

		@Override
		public void processElement(Integer value, KeyedProcessFunction<Integer, Integer, Long>.Context ctx,
				Collector<Long> out) throws Exception {
			Thread.sleep(1);
			
			int preTemp = 0;
			if(lastTemp.value() != null) {
				preTemp = lastTemp.value();
			}			
			lastTemp.update(value);
			
			long currentTimerTimstamp = 0;
			if(currentTimer.value() != null) {
				currentTimerTimstamp = currentTimer.value();
			}

			if (preTemp < value) {
				if (currentTimerTimstamp == 0) {
					long timerTs = ctx.timerService().currentProcessingTime() + 10;
					ctx.timerService().registerProcessingTimeTimer(timerTs);
					currentTimer.update(timerTs);
				}
			} else {
				ctx.timerService().deleteEventTimeTimer(currentTimer.value());
				currentTimer.clear();
			}
		}

		@Override
		public void onTimer(long timestamp, OnTimerContext ctx, Collector<Long> out) throws Exception {
			long counterI = counter.value();
			counter.update(counterI+1);
			out.collect(timestamp);
			out.collect(counterI);
			currentTimer.clear();
		}

	}
}
