package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction.Context;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class CustomizedWindowTest {

	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		// make sure watermark updated as quickly as possible
		env.getConfig().setAutoWatermarkInterval(1L);
		
		env.setParallelism(1);
		IntStream is = IntStream.range(1, 6000);// .of(1, 2, 3, 4, 5, 6);

		DataStreamSource<Integer> source = env.fromCollection(is.boxed().collect(Collectors.toList()));
		
		SingleOutputStreamOperator<Integer> withTimestampsAndWatermarks = source.assignTimestampsAndWatermarks(
				WatermarkStrategy.<Integer>forMonotonousTimestamps()
				.withTimestampAssigner((obj, recordTimestamp)-> obj ));
		
		// 1, 11, 21, 31....will be grouped into same key
		KeyedStream<Integer, Integer> keyedStream = withTimestampsAndWatermarks.keyBy(key -> key%10);
		
		WindowedStream<Integer, Integer, TimeWindow> windowedStream = keyedStream.window(new ThreeSecondsWindowAssigner());
		
		windowedStream = windowedStream.trigger(new OneSecondIntervalTriger());
		
		 SingleOutputStreamOperator<Tuple4<Integer, Long, Long, Integer>> outputStream = windowedStream.process(new CountFunction());
		 
		 outputStream.print();
		 
		 env.execute("Run custom window example");

	}

	public static class ThreeSecondsWindowAssigner extends WindowAssigner<Object, TimeWindow> {

		 long windowSize = 3_000L;
		 
		@Override
		public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
			 // rounding down by 30 seconds
            long startTime = timestamp - (timestamp % windowSize);
            long endTime = startTime + windowSize;
            // emitting the corresponding time window
            return Collections.singletonList(new TimeWindow(startTime, endTime));
		}

		@Override
		public Trigger<Object, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
			return EventTimeTrigger.create();
		}

		@Override
		public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
			 return new TimeWindow.Serializer();
		}

		@Override
		public boolean isEventTime() {			
			return true;
		}
		
	}
	
	public static class OneSecondIntervalTriger extends Trigger<Integer, TimeWindow> {

		@Override
		public TriggerResult onElement(Integer element, long timestamp, TimeWindow window, TriggerContext ctx)
				throws Exception {
			  // firstSeen will be false if not set yet
            ValueState<Boolean> firstSeen = ctx.getPartitionedState(
                new ValueStateDescriptor<>("firstSeen", Types.BOOLEAN));

            // register initial timer only for first element
            if (firstSeen.value() == null) {
                // at the very beginning, getCurrentWatermark() returns LONG_MIN
            	long safeCurrentWatermark = ctx.getCurrentWatermark();
            	if(safeCurrentWatermark < 0) {
            		safeCurrentWatermark = 0;
            	}
            	// compute time for next early firing by rounding watermark to second
                long t = safeCurrentWatermark + (1000 - (safeCurrentWatermark % 1000));
                System.out.println("set timer on: " + t + ", for element: " + element);
                ctx.registerEventTimeTimer(t);
                // register timer for the end of the window
                ctx.registerEventTimeTimer(window.getEnd());
                firstSeen.update(true);
            }
            // Continue. Do not evaluate window per element
            return TriggerResult.CONTINUE;
		}

		@Override
		public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
			 // Continue. We don't use processing time timers
            return TriggerResult.CONTINUE;
		}

		@Override
		public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
			  if (time == window.getEnd()) {
	                // final evaluation and purge window state
	                return TriggerResult.FIRE_AND_PURGE;
	            } else {
	            	 System.out.println("onEventTime");
	                // register next early firing timer
	                long t = ctx.getCurrentWatermark() + (1000 - (ctx.getCurrentWatermark() % 1000));
	                // when stream ends, getCurrentWatermark will return LONG_MAX
	                if (t < window.getEnd() && t >= 0) {
	                    ctx.registerEventTimeTimer(t);
	                    System.out.println("set timer on: " + t + ", getCurrentWatermark: " + ctx.getCurrentWatermark());
	                }
	                // fire trigger to early evaluate window
	                return TriggerResult.FIRE;	               
	            }
		}

		@Override
		public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
			   // clear trigger state
            ValueState<Boolean> firstSeen = ctx.getPartitionedState(
                new ValueStateDescriptor<>("firstSeen", Types.BOOLEAN));
            firstSeen.clear();
		}
		
	}
	
	/**
     * A window function that counts the readings per sensor and window.
     * The function emits the sensor id, window end, tiem of function evaluation, and count.
     */
    public static class CountFunction
            extends ProcessWindowFunction<Integer, Tuple4<Integer, Long, Long, Integer>, Integer, TimeWindow> {

		@Override
		public void process(Integer key,
				ProcessWindowFunction<Integer, Tuple4<Integer, Long, Long, Integer>, Integer, TimeWindow>.Context context,
				Iterable<Integer> elements, Collector<Tuple4<Integer, Long, Long, Integer>> out) throws Exception {
			 // count readings
            int cnt = 0;
            for (Integer i : elements) {
                cnt++;
            }
            // get current watermark
            long evalTime = context.currentWatermark();
            // emit result
            out.collect(Tuple4.of(key, context.window().getEnd(), evalTime, cnt));
            //System.out.println("CountFunction");
		}
    }
}
