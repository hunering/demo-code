package com.hxm.dcm.utils.powergen.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hxm.dcm.common.msg.kafka.KafkaProperty;
import com.hxm.dcm.common.msg.kafka.PowerMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxm.dcm.common.msg.kafka.PowerMsgUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.time.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PowerDataGen {
	public static void main(String[] args) throws ExecutionException, InterruptedException, JsonProcessingException {
		PowerDataGen gen = new PowerDataGen();
		Producer<String, String> producer = gen.createKafkaProducer(KafkaProperty.BootstrapServers);
		String deviceId = "device_id_0";
		ZoneId shZone = ZoneId.of("+8");
		ZonedDateTime timeZoned = ZonedDateTime.of(LocalDateTime.now(), shZone);
		gen.genKafkaPowerData(producer, deviceId, timeZoned, 100);
		producer.close();
	}

	public Producer<String, String> createKafkaProducer(String servers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", servers);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return new KafkaProducer<>(props);
	}

	public void genKafkaPowerData(Producer<String, String> producer, String deviceId, ZonedDateTime timeZoned, int value) throws JsonProcessingException, ExecutionException, InterruptedException {
		ObjectMapper objectMapper = PowerMsgUtils.createMapper();
		for(int i = 0; i < 200; i++) {
			PowerMsg msg = new PowerMsg(deviceId, timeZoned, value);
			String strMsg = PowerMsgUtils.Msg2Json(msg, objectMapper);
			Future<RecordMetadata> data = producer.send(new ProducerRecord<String, String>(KafkaProperty.PowerTopicName, deviceId, strMsg));
			data.get();
		}
	}
}
