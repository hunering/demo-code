package com.xmo.demo.java.alg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FindTopK {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static List<String> topK(int k, Iterator<String> it){
		ArrayList<String> buf = new ArrayList<>(10000000);
		while(it.hasNext()) {
			String item = it.next();
			buf.set(item.length(), item);
		}
		ArrayList<String> result = new ArrayList<>(k);
		int counts = 0;
		for(int i = buf.size()-1; i >= 0 && counts < k; i--) {
			if(buf.get(i) != null) {
				result.add(buf.get(i));
				counts ++;
			}
		}
		return result;
	}
}
