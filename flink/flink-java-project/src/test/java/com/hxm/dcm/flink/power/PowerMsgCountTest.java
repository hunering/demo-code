package com.hxm.dcm.flink.power;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PowerMsgCountTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testSavepoint() {
		// 1. clean the dcm_power topic in kafka
		// 2. submit the PowerMsgCount job to flink
		// using the data in resources/power/power_msg_count
		// 3. produce the power messages to kafka topic in power_data_1.txt
		// 4. task a savepoint of the flink job
		// 5. produce the part2 messages in power_data_2.txt
		// 6. restore the PowerMsgCount using savepoint created in step 4
		// 7. produce the part3 message in power_data_3.txt to move forward the watermark
		// result: there should be 4 messages in window 2020-11-26T10:45
	}
}