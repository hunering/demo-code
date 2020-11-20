package com.hxm.dcm.utils.powergen.kafka;

import com.hxm.dcm.common.msg.kafka.PowerMsg;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PowerDataGen {
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9093");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		//props.put("value.serializer", "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer");

		ObjectMapper objectMapper = new ObjectMapper();
		Producer<String, String> producer = new KafkaProducer<>(props);
		//Producer<String, PowerMsg> producer = new KafkaProducer<>(props);
		for(int i = 0; i < 100; i++) {
			String deviceId = "device_id_0";
			PowerMsg msg = new PowerMsg(deviceId, LocalDateTime.now(), 100);
			JsonNode jsonMsg = objectMapper.valueToTree(msg);
			Future<RecordMetadata> data = producer.send(new ProducerRecord<String, String>("dcm_power", deviceId, jsonMsg.toString()));
			data.get();
			//producer.send(new ProducerRecord<String, PowerMsg>("dcm-power", deviceId, msg));
		}


		producer.close();
	}
}
