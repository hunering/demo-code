package com.intel.java7.collection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class ArrayListTest {

	public static void main(String[] args) {
		ArrayList<Integer> al = new ArrayList<Integer>();
		al.add(1);
		al.add(1, 2);
		
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

}
