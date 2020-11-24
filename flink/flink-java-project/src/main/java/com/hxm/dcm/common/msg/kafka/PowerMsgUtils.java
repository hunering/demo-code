package com.hxm.dcm.common.msg.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PowerMsgUtils {
	public static String Msg2Json(PowerMsg msg, ObjectMapper objectMapper) throws JsonProcessingException {
		return objectMapper.writeValueAsString(msg);
	}

	public static String Msg2Json(PowerMsg msg) throws JsonProcessingException {
		ObjectMapper objectMapper = createMapper();
		return Msg2Json(msg, objectMapper);
	}

	public static PowerMsg Json2Msg(String json, ObjectMapper objectMapper) throws JsonProcessingException {
		return objectMapper.readValue(json, PowerMsg.class);
	}

	public static PowerMsg Json2Msg(String json) throws JsonProcessingException {
		ObjectMapper objectMapper = createMapper();
		return Json2Msg(json, objectMapper);
	}

	public static ObjectMapper createMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
}
