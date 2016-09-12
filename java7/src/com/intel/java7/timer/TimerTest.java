package com.intel.java7.timer;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {
	public static void main(String... args) {
		Timer timer = new Timer();
		
		WaitHandler wh = new WaitHandler();
		timer.schedule(new WaitHandler(), 1000);
		
		System.out.println("After timer");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timer.cancel();
	}
	
	
	
	static class WaitHandler extends TimerTask {
		@Override
		public void run() {
			System.out.println("in the WaitHandler");
		}
	}
}
