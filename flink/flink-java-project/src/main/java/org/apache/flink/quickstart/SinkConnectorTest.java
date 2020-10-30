package org.apache.flink.quickstart;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.quickstart.StateTest.MyFunction;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink.DefaultRowFormatBuilder;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.runtime.operators.CheckpointCommitter;
import org.apache.flink.streaming.runtime.operators.GenericWriteAheadSink;

public class SinkConnectorTest {

	public static void main(String[] args) throws Exception {
		//testFileSinkConnector();
		testWriteAheadSink();
	}
	
	public static void testFileSinkConnector() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		
		//HAVE TO ENABKE CHECKPOINT
		env.enableCheckpointing(100);
		
		String fileSource = "file://" + System.getProperty("user.dir") + "/source_files";
		TextInputFormat lineReader = new TextInputFormat(null);
		DataStreamSource<String> source = env.readFile(lineReader, fileSource,  FileProcessingMode.PROCESS_CONTINUOUSLY, 1000);
		
		SingleOutputStreamOperator<String> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> {
							return Integer.parseInt(obj.split(",")[0]);
						}));

		SingleOutputStreamOperator<String> sos = withTimestampsAndWatermarks
				.keyBy(value -> value.hashCode() % 10)
				.map(new MyMapFunction());

		String fileFolder = "file://" + System.getProperty("user.dir") + "/sink_files";
		StreamingFileSink<String> fileSink = StreamingFileSink
				.forRowFormat(new org.apache.flink.core.fs.Path(fileFolder), new SimpleStringEncoder<String>("UTF-8"))
				.withRollingPolicy(
				        DefaultRollingPolicy.builder()
				            .withRolloverInterval(TimeUnit.SECONDS.toMillis(15))
				            .withInactivityInterval(TimeUnit.SECONDS.toMillis(5))
				            .withMaxPartSize(1024 * 1024 * 1024)
				            .build())
				.build();
		
		sos.addSink(fileSink);
		sos.print();
		env.execute("testFileSinkConnector");			
	}
	
	public static class MyMapFunction implements MapFunction<String, String> {

		@Override
		public String map(String value) throws Exception {
			String valueColumn = value.split(",")[1];
			return "value:"+valueColumn;
		}
		
	}

	public static void testWriteAheadSink() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		env.enableCheckpointing(10000);
		env.getCheckpointConfig().setCheckpointTimeout(1000000);
		StateBackend backend = new MemoryStateBackend();
		env.setStateBackend(backend);
		backend = env.getStateBackend();

		DataStreamSource<Long> source = env.addSource(new CountSourceFunction());
		SingleOutputStreamOperator<String> sos = source
				.keyBy(value -> value % 10)
				.map(value -> {return "Value：" + value;});

		sos.transform("WriteAheadSink",
				TypeInformation.of(String.class),
				new StdOutWriteAheadSink())
			.setParallelism(1);

		env.execute("testWriteAheadSink");
	}

	public static class StdOutWriteAheadSink extends GenericWriteAheadSink<String> {
		public StdOutWriteAheadSink() throws Exception {
			super(new FileCheckpointCommitter(System.getProperty("user.dir") + "/SinkCommitter_files"),
					TypeInformation.of(String.class).createSerializer(new ExecutionConfig()),
					java.util.UUID.randomUUID().toString());
		}

		@Override
		protected boolean sendValues(Iterable<String> values, long checkpointId, long timestamp) throws Exception {
			for(String value:values) {
				System.out.println(value);
			}
			return true;
		}
	}

	public static class FileCheckpointCommitter extends CheckpointCommitter {
		private String path;

		public FileCheckpointCommitter(String path) {
			this.path = path;
		}

		@Override
		public void open() throws Exception {

		}

		@Override
		public void close() throws Exception {

		}

		@Override
		public void createResource() throws Exception {
			this.path = path + "/" + this.jobId+"/";
			// create directory for commit file
			Files.createDirectories(Paths.get(path));
			//Files.createDirectory(Paths.get(path));
		}

		@Override
		public void commitCheckpoint(int subtaskIdx, long checkpointID) throws Exception {
			Path commitPath = Paths.get(path + "/" + subtaskIdx);

			// convert checkpointID to hexString
			String hexID = "0x" + StringUtils.leftPad(""+checkpointID, 16, "0");
			// write hexString to commit file
			Files.write(commitPath, hexID.getBytes());
		}

		@Override
		public boolean isCheckpointCommitted(int subtaskIdx, long checkpointID) throws Exception {
			Path commitPath = Paths.get(path + "/" + subtaskIdx);

			if (!Files.exists(commitPath)) {
				// no checkpoint has been committed if commit file does not exist
				return false;
			} else {
				// read committed checkpoint id from commit file
				String hexID = Files.readAllLines(commitPath).get(0);
				Long checkpointed = Long.decode(hexID);
				// check if committed id is less or equal to requested id
				return checkpointID <= checkpointed;
			}
		}
	}

	public static class CountSourceFunction implements SourceFunction<Long> {

		private volatile boolean isRunning = true;

		@Override
		public void run(SourceContext<Long> ctx) throws Exception {
			long cnt = -1;
			while (isRunning && cnt < Long.MAX_VALUE) {
				Thread.sleep(100);
				cnt += 1;
				ctx.collect(cnt);
			}
		}

		@Override
		public void cancel() {
			isRunning = false;
		}
	}
}
