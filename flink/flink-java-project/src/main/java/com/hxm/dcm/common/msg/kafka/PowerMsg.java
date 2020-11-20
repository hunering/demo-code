package com.hxm.dcm.common.msg.kafka;

import java.time.LocalDateTime;

public class PowerMsg {
	private String deviceId;
	private LocalDateTime timeStamp;
	private float value;

	public PowerMsg(String deviceId, LocalDateTime timeStamp, float value) {
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

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
}
