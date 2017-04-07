package com.intel.java7.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ArrayListTest {

	public static void main(String[] args) {
		convert2Array();
		ArrayList<Integer> al = new ArrayList<Integer>();
		al.add(1);
		al.add(1, 2);
		//al.ensureCapacity(10);
		Iterator<Integer> it = al.iterator();
		while(it.hasNext()){
			int value = it.next();
			System.out.println(value);
			// al.add(3); // will case it.next throw exception
		}
		
		Vector<Integer> vec = new Vector<Integer>();
		vec.add(1);
		vec.add(1, 2);
		
		Iterator<Integer> it2 = vec.iterator();
		while(it2.hasNext()){
			int value = it2.next();
			System.out.println(value);
			//vec.add(3); // will case it.next throw exception
		}

		Enumeration<Integer> enu = vec.elements();
		int i = 3;
		while(enu.hasMoreElements()) {
			int value = enu.nextElement();
			System.out.println(value);
			// vec.add(i++); // dead loop, never break from the while
		}
	}
	
	public static void convert2Array(){
		ArrayList<Integer> al = new ArrayList<Integer>();
		al.add(1);
		al.add(2);
		
		Object[] array = al.toArray();
		//Integer[] array = (Integer[]) al.toArray();
		array[0] = 10;
		
		Integer[] array2 = new Integer[al.size()];
		al.toArray(array2);
		array2[0] += 10;
		
		for(int i : al) {
			System.out.println(i);
		}
		
		List<String> assetList = new ArrayList();
		String[] asset = {"equity", "stocks", "gold", "foriegn exchange", "fixed income", "futures", "options"}; 
		Collections.addAll(assetList, asset);


		List<String> assetList2 = Arrays.asList(asset); 
		
		//assetList2.add("abc"); // will throw exception
	}

}
