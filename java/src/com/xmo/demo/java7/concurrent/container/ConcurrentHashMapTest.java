package com.xmo.demo.java7.concurrent.container;

import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {

	public static void main(String[] args) {
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

		Runnable writeTask = () -> {
			System.out.println("writeTask is running");
			int i = 0;
			while (true) {
				i++;
				Set<Entry<String, String>> entrySet = map.entrySet();
				for (Entry<String, String> entry : entrySet) {
					entry.setValue("value: " + i);
					//System.out.println(entry);
				}
			}
		};

		Runnable readTask = () -> {
			System.out.println("readTask is running");
			int i = 0;
			while (true) {
				if (i > 15) {
					i = 0;
				}
				i++;
				String key = "" + i;
				if (map.containsKey(key)) {
					System.out.println("key:" + key + ", value: " + map.get(key));
				} else {
					map.put(key, "original");
				}
			}
		};
		
		Runnable deleteTask = () -> {
			System.out.println("deleteTask is running");
			int i = 0;
			while (true) {
				i++;

				if(i%100 == 0) {
					Random rand = new Random();
					int some = rand.nextInt(15);
					String key = "" + some;
					map.remove(key);
				}
			}
		};
		
		new Thread(writeTask).start();
		new Thread(readTask).start();
		new Thread(deleteTask).start();
	}

}
