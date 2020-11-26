package com.hxm.dcm.common.msg.influxdb;

public class InfluxdbProperty {
	public static final String influxdbServers = "http://localhost:8086";
	public static final String username = "user";
	public static final String password = "intel_123";
	public static final String dbName = "dcm";

	public static final String PowerMsgCountMeasureName = "dcm_power_msg_count";
	public static final String PowerMsgCountValueField = "value";

	public static final String PowerAverageMeasureName = "dcm_power_avg_";
	public static final String PowerAverageValueField = "value";
}
