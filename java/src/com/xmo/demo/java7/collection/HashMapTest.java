package com.xmo.demo.java7.collection;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HashMapTest {
	
	public static void main(String... args) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int keys[] = {1,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
		
		for(int i:keys) {
			map.put(i, i);
		}
		map.put(null, Integer.MAX_VALUE);
		System.out.println("key: null, value: " + map.get(null));
		
		Integer previousValue = map.replace(2, 2);
		
		Set<Entry<Integer, Integer>> entrySet = map.entrySet();
		for(Entry<Integer, Integer> entry : entrySet) {
			System.out.println("Key:" + entry.getKey() + "; Value:" + entry.getValue());
			// map.put(3, 3); //will cause the for-statement throw java.util.ConcurrentModificationException
			// map.remove(entry.getKey()); will case the for-statement throw java.util.ConcurrentModificationException
			
			//map.put(entry.getKey(), entry.getValue()+1);
			//break;
		}
		
		//map.put(3, 3);
		Iterator<Map.Entry<Integer,Integer>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<Integer,Integer> entry = iter.next();
		    System.out.println("using iterator: Key:" + entry.getKey() + "; Value:" + entry.getValue());
		    if(new Integer(1).equals(entry.getValue())){
		    	System.out.println("1 is removed from the HashMap");
		        iter.remove();
		    }
		}
		
		
		Integer value = map.get(1);
		System.out.println("Key:" + 1 + "; Value:" + value);
		
		
		System.out.println("Going to the Hashtable");
		Hashtable<Integer, Integer> hashTable = new Hashtable<Integer, Integer>();
		for(int i:keys) {
			hashTable.put(i, i);
		}
		// hashTable.put(null, Integer.MAX_VALUE);
		
		for(Entry<Integer, Integer> entry : hashTable.entrySet()) {
			System.out.println("Key:" + entry.getKey() + "; Value:" + entry.getValue());
			Integer beforeRemove = entry.getValue();
			// hashTable.put(3, 3); //will throw java.util.ConcurrentModificationException
			// hashTable.remove(entry.getKey()); //will throw java.util.ConcurrentModificationException
			hashTable.put(entry.getKey(), beforeRemove);			
		}
		
		
		Iterator<Map.Entry<Integer,Integer>> iter2 = hashTable.entrySet().iterator();
		while (iter2.hasNext()) {
		    Map.Entry<Integer,Integer> entry = iter2.next();
		    if(new Integer(1).equals(entry.getValue())){
		    	System.out.println("1 is removed from the Hashtable");
		        iter.remove();
		    }
		}

		
	}
}
