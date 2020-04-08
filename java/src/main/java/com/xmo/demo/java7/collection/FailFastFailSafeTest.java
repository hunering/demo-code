package com.xmo.demo.java7.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class FailFastFailSafeTest {
	public static void main(String[] args) {
		ConcurrentHashMap<String, String> premiumPhone = new ConcurrentHashMap<String, String>();
		premiumPhone.put("Apple", "iPhone6");
		premiumPhone.put("HTC", "HTC one");
		premiumPhone.put("Samsung", "S6");

		Iterator<String> iterator = premiumPhone.keySet().iterator();

		while (iterator.hasNext()) {
			System.out.println(premiumPhone.get(iterator.next()));
			premiumPhone.put("Sony", "Xperia Z");
		}

		HashMap<String, String> hashPhone = new HashMap<String, String>();
		hashPhone.put("Apple", "iPhone6");
		hashPhone.put("HTC", "HTC one");
		hashPhone.put("Samsung", "S6");

		Iterator<String> iterator2 = hashPhone.keySet().iterator();

		while (iterator2.hasNext()) {
			System.out.println(hashPhone.get(iterator2.next()));
			hashPhone.put("Sony", "Xperia Z"); // next call to hasNext will throw ConcurrentModificationException
		}
	}
}
