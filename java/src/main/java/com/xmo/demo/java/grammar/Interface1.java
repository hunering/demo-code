package com.xmo.demo.java.grammar;

public interface Interface1 {
	// The following line case compiling problem
	//public default String toString() {
	//	return "";
	//};
	
	public default String sameName() {
		return  "Interface1";
	};
}
