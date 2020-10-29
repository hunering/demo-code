package org.apache.flink.quickstart;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.quickstart.StateTest.MyFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink.DefaultRowFormatBuilder;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;

public class SinkConnectorTest {

	public static void main(String[] args) throws Exception {
		testFileSinkConnector();

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
		env.execute("testFileSinkConnector");			
	}
	
	public static class MyMapFunction implements MapFunction<String, String> {

		@Override
		public String map(String value) throws Exception {
			String valueColumn = value.split(",")[1];
			return "value:"+valueColumn;
		}
		
	}

}
