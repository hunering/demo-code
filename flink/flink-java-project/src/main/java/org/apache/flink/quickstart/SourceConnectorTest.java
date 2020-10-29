package org.apache.flink.quickstart;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.quickstart.StateTest.MyFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;

public class SourceConnectorTest {

	public static void main(String[] args) throws Exception {
		testFileSourceConnector();

	}
	
	public static void testFileSourceConnector() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(1);
		env.getConfig().setAutoWatermarkInterval(1L);
		
		String fileSource = "file://" + System.getProperty("user.dir") + "/source_files";
		TextInputFormat lineReader = new TextInputFormat(null);
		DataStreamSource<String> source = env.readFile(lineReader, fileSource,  FileProcessingMode.PROCESS_ONCE, 1000);
		
		SingleOutputStreamOperator<String> withTimestampsAndWatermarks = source
				.assignTimestampsAndWatermarks(WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofMillis(1))
						.withTimestampAssigner((obj, recordTimestamp) -> {
							return Integer.parseInt(obj.split(",")[0]);
						}));

		SingleOutputStreamOperator<Integer> sos = withTimestampsAndWatermarks
				.keyBy(value -> value.hashCode() % 10)
				.map(new MyMapFunction());

		sos.print();

		env.execute("testFileSourceConnector");			
	}
	
	public static class MyMapFunction implements MapFunction<String, Integer> {

		@Override
		public Integer map(String value) throws Exception {
			String valueColumn = value.split(",")[1];
			return valueColumn.length();
		}
		
	}

}
