package com.xmo.demo.java7.collection;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListTest {

	public static void main(String[] args) {
		CopyOnWriteArrayList<Integer> cow = new CopyOnWriteArrayList<Integer>();
		cow.add(1);
		cow.add(2);
		
		for(int i : cow) {
			System.out.println(i);
			cow.add(3); // will not make iterator.next throw exception
		}
		
		Iterator<Integer> it = cow.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
			//it.remove(); // throw UnsupportedOperationException
		}
	}

}
