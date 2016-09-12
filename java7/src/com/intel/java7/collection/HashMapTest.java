package com.intel.java7.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapTest {
	
	public static void main(String... args) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int keys[] = {1,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
		
		for(int i:keys) {
			map.put(i, i);
		}
		
		for(Entry<Integer, Integer> entry : map.entrySet()) {
			System.out.println("Key:" + entry.getKey() + "; Value:" + entry.getValue());
		}
		
		Integer value = map.get(1);
		System.out.println("Key:" + 1 + "; Value:" + value);
		
	}
}
