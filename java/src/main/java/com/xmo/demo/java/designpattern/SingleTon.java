package com.xmo.demo.java.designpattern;

public enum SingleTon {
	INSTANCE;
	
	int value = 0;
	public void setValue(int i) {
		this.value = i;
	}
	public int getValue() {
		return this.value;
	}
}
