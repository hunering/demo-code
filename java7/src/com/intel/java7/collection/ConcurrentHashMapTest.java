package com.intel.java7.collection;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {

	public static void main(String[] args) {
		ConcurrentHashMap<Integer, Integer> chm = new ConcurrentHashMap<Integer, Integer>();

		int keys[] = {1,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
		
		for(int i:keys) {
			chm.put(i, i);
		}
		
		// The following line will throw exception
		// chm.put(null, Integer.MAX_VALUE);
		// System.out.println("key: null, value: " + chm.get(null));
		
		Integer previousValue = chm.replace(2, 2);
		
		Set<Entry<Integer, Integer>> entrySet = chm.entrySet();
		for(Entry<Integer, Integer> entry : entrySet) {
			System.out.println("Key:" + entry.getKey() + "; Value:" + entry.getValue());
			//entry.setValue(11111);
			chm.put(3, 3); 
			chm.putIfAbsent(3, 4);
			//chm.remove(entry.getKey()); 
			chm.remove(6);
			System.out.println("If contains 6? " + chm.contains(6));
		}
		
		Iterator<Map.Entry<Integer,Integer>> iter = chm.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<Integer,Integer> entry = iter.next();
		    System.out.println("using iterator: Key:" + entry.getKey() + "; Value:" + entry.getValue());
		    if(new Integer(1).equals(entry.getValue())){
		    	System.out.println("1 is removed from the HashMap");
		        iter.remove();
		    }
		}
		
		
		Integer value = chm.get(1);
		System.out.println("Key:" + 1 + "; Value:" + value);
		
	}

}
