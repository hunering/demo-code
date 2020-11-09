package org.apache.flink.quickstart;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.nio.file.StandardCopyOption.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.quickstart.StateTest.MyFunction;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.AsyncFunction;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink.DefaultRowFormatBuilder;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.runtime.operators.CheckpointCommitter;
import org.apache.flink.streaming.runtime.operators.GenericWriteAheadSink;

import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

public class SinkConnectorTest {

	public static void main(String[] args) throws Exception {
		//testFileSinkConnector();
		//testWriteAheadSink();
		//testTwoPhaseCommitSink();
		testAsyncFunction();
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

	public static void testTwoPhaseCommitSink() throws Exception {
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

		sos.addSink(new TransactionalFileSink(StringSerializer.INSTANCE, VoidSerializer.INSTANCE))
				.setParallelism(1);

		env.execute("testTwoPhaseCommitSink");
	}

	public static class TransactionalFileSink extends TwoPhaseCommitSinkFunction<String, String, Void> {
		private BufferedWriter transactionWriter;
		private static String transactionalFileDir = System.getProperty("user.dir") + "/twoPhaseCommitFile";

		public TransactionalFileSink(TypeSerializer<String> transactionSerializer, TypeSerializer<Void> contextSerializer) {
			super(transactionSerializer, contextSerializer);
		}

		@Override
		protected void invoke(String transaction, String value, Context context) throws Exception {
			transactionWriter.write(value);
			transactionWriter.write('\n');
		}

		@Override
		protected String beginTransaction() throws Exception {
			DateTimeFormatterBuilder timeBuilder = new DateTimeFormatterBuilder()
					.appendValue(YEAR, 4)
					.appendLiteral('-')
					.appendValue(MONTH_OF_YEAR, 2)
					.appendLiteral('-')
					.appendValue(DAY_OF_MONTH, 2)
					.appendLiteral('-')
					.appendValue(HOUR_OF_DAY, 2)
					.appendLiteral('-')
					.appendValue(MINUTE_OF_HOUR, 2)
					.appendLiteral('-')
					.appendValue(SECOND_OF_MINUTE, 2)
					.appendLiteral('-')
					.appendFraction(NANO_OF_SECOND, 0, 9, false);

			LocalDateTime timeNow = LocalDateTime.now(ZoneId.of("UTC"));
			String timeNowStr = timeNow.format(timeBuilder.toFormatter());
			int taskIdx = this.getRuntimeContext().getIndexOfThisSubtask();
			String transactionFile = String.format("%s-%d", timeNowStr, taskIdx);
			Path tFilePath = Paths.get(transactionalFileDir,"temp", transactionFile);
			Files.createFile(tFilePath);
			this.transactionWriter = Files.newBufferedWriter(tFilePath);
			System.out.println("Creating Transaction File: " + tFilePath);
			return transactionFile.toString();
		}

		@Override
		protected void preCommit(String transaction) throws Exception {
			transactionWriter.flush();
			transactionWriter.close();
		}

		@Override
		protected void commit(String transaction) {
			try {
				Path tFilePath = Paths.get(transactionalFileDir,"temp", transaction);
				// check if the file exists to ensure that the commit is idempotent
				if (Files.exists(tFilePath)) {
					Path cFilePath = Paths.get(transactionalFileDir,"target", transaction);
					Files.move(tFilePath, cFilePath, REPLACE_EXISTING);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void abort(String transaction) {
			try {
				Path tFilePath = Paths.get(transactionalFileDir,"temp");
				if (Files.exists(tFilePath)) {
					Files.delete(tFilePath);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static void testAsyncFunction() throws Exception {
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

		SingleOutputStreamOperator<String> asos = AsyncDataStream.orderedWait(sos, new DerbyAsyncFunction(),
				5, TimeUnit.SECONDS, 1);
		asos.print();
		env.execute("testAsyncFunction");
	}

	public static class DerbyAsyncFunction extends RichAsyncFunction<String, String> {

		// caching execution context used to handle the query threads
		private transient ExecutorService cachingPoolExecutor = null;

		@Override
		public void open(Configuration parameters) throws Exception {
			cachingPoolExecutor= Executors.newCachedThreadPool();
		}

		@Override
		public void asyncInvoke(String input, ResultFuture<String> resultFuture) throws Exception {

			Supplier<String> task = () -> {
				return input;
			};

			CompletableFuture<String> future = CompletableFuture.supplyAsync(task, cachingPoolExecutor);
			future.exceptionally((e) -> {
				resultFuture.complete(Collections.singleton(e.toString()));
				return e.toString();
			}).thenAccept((String value)-> {
				resultFuture.complete(Collections.singleton(value));
			});

		}
	}
}
