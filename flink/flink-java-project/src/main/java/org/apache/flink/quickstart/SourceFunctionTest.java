package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.quickstart.SourceConnectorTest.MyMapFunction;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

public class SourceFunctionTest {

	public static void main(String[] args) throws Exception {
		// testSourceFunction();
		testCheckPointedSourceFunction();

	}

	public static void testSourceFunction() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		env.enableCheckpointing(100);

		DataStreamSource<Long> source = env.addSource(new CountSourceFunction());

		SingleOutputStreamOperator<Long> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<Long>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> {
							return obj;
						}));

		SingleOutputStreamOperator<String> sos = withTimestampsAndWatermarks
				.keyBy(value -> value.hashCode() % 10)
				.map(value -> "String:" + value);

		sos.print();

		env.execute("testSourceFunction");
	}

	public static class CountSourceFunction implements SourceFunction<Long> {

		private volatile boolean isRunning = true;

		@Override
		public void run(SourceContext<Long> ctx) throws Exception {
			long cnt = -1;
			while (isRunning && cnt < Long.MAX_VALUE) {
				Thread.sleep(1000);
				cnt += 1;
				ctx.collect(cnt);
			}
		}

		@Override
		public void cancel() {
			isRunning = false;
		}
	}

	public static void testCheckPointedSourceFunction() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		env.enableCheckpointing(100);
		env.getCheckpointConfig().setCheckpointTimeout(1000000);
		StateBackend backend = new MemoryStateBackend();
		env.setStateBackend(backend);
		backend = env.getStateBackend();

		DataStreamSource<Long> source = env.addSource(new CheckPointedCountSourceFunction());
		//DataStreamSource<Long> source = env.addSource(new ExampleCountSource());
		
		/*
		 * SingleOutputStreamOperator<Long> withTimestampsAndWatermarks = source
		 * .assignTimestampsAndWatermarks(WatermarkStrategy.<Long>
		 * forBoundedOutOfOrderness(Duration.ofMillis(1)) .withTimestampAssigner((obj,
		 * recordTimestamp) -> { return obj; }));
		 * 
		 * SingleOutputStreamOperator<String> sos = withTimestampsAndWatermarks
		 * .keyBy(value -> value.hashCode() % 10) .map(value -> "String:" + value);
		 */
		SingleOutputStreamOperator<String> sos = source
				.keyBy(value -> value.hashCode() % 10)
				.map(value -> "String:" + value);

		String fileFolder = "file://" + System.getProperty("user.dir") + "/sink_files";
		StreamingFileSink<String> fileSink = StreamingFileSink
				.forRowFormat(new Path(fileFolder), new SimpleStringEncoder<String>("UTF-8"))
				.withRollingPolicy(
						DefaultRollingPolicy.builder()
								.withRolloverInterval(TimeUnit.SECONDS.toMillis(15))
								.withInactivityInterval(TimeUnit.SECONDS.toMillis(5))
								.withMaxPartSize(1024 * 1024 * 1024)
								.build())
				.build();

		sos.addSink(fileSink);
		sos.print();

		env.execute("testCheckPointedSourceFunction");
	}

	public static class CheckPointedCountSourceFunction implements SourceFunction<Long>, CheckpointedFunction {
		private static final long serialVersionUID = 5830656954002750461L;
		private volatile boolean isRunning = true;
		private ListState<Long> offsetState;
		private long cnt = -1;

		public CheckPointedCountSourceFunction() {
			System.out.println("inside CheckPointedCountSourceFunction");
		}

		@Override
		public void run(SourceContext<Long> ctx) throws Exception {
			synchronized (ctx.getCheckpointLock()) {
			while (isRunning && cnt < Long.MAX_VALUE) {
				Thread.sleep(1000);

					cnt += 1;
					ctx.collect(cnt);
					// ctx.emitWatermark(new Watermark(cnt));
				}				
			}
		}
		

		@Override
		public void cancel() {
			isRunning = false;
		}

		@Override
		public void snapshotState(FunctionSnapshotContext context) throws Exception {
			try {
				offsetState.clear();
				offsetState.add(cnt);
			} catch (Exception e) {

			}
		}

		@Override
		public void initializeState(FunctionInitializationContext context) throws Exception {
			offsetState = context.getOperatorStateStore().getListState(
					new ListStateDescriptor<>("offsetState", Long.class));
			if (offsetState.get() != null && offsetState.get().iterator().hasNext()) {
				cnt = offsetState.get().iterator().next();
			} else {
				cnt = -1;
			}
		}
	}
}
