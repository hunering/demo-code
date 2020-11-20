package com.hxm.dcm.common.msg.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class PowerMsg {
	private String deviceId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private ZonedDateTime timeStamp;
	private float value;

	public PowerMsg(String deviceId, ZonedDateTime timeStamp, float value) {
		this.deviceId = deviceId;
		this.timeStamp = timeStamp;
		this.value = value;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public ZonedDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(ZonedDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
