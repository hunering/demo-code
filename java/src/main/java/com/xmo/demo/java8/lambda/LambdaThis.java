package com.xmo.demo.java8.lambda;

import java.util.function.DoubleSupplier;

public class LambdaThis {
	double value = 1.234;

	public DoubleSupplier createLambdaInterface() {
		DoubleSupplier si = () -> {
			double value = 5.678;
			return this.value;
		};

		return si;
	}

	public DoubleSupplier createAnonymousInterface() {
		DoubleSupplier si = new DoubleSupplier() {
			double value = 5.678;
			@Override
			public double getAsDouble() {
				return this.value;
			}
		};

		return si;
	}

	public static void main(String[] args) {
		LambdaThis lt = new LambdaThis();

		DoubleSupplier lambdaInterface = lt.createLambdaInterface();
		DoubleSupplier anonymousInterface = lt.createAnonymousInterface();

		System.out.println("Lambda interface value : " + lambdaInterface.getAsDouble());
		System.out.println("Anonymous interface value : " + anonymousInterface.getAsDouble());
	}
}
