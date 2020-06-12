package com.xmo.demo.java7.concurrent.forkjoinpool;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class ArrayElementSumForkJoin {

	static class ForkJoinSumCalculator extends RecursiveTask<Long> {
		private final long[] elements;
		private final int start;
		private final int end;

		public ForkJoinSumCalculator(long[] elements, int start, int end) {
			this.elements = elements;
			this.start = start;
			this.end = end;
		}

		@Override
		protected Long compute() {
			try {
				int length = end - start;
				if (length > 1000) {
					ForkJoinSumCalculator left = new ForkJoinSumCalculator(elements, start, start + length / 2);
					left.fork();
					ForkJoinSumCalculator right = new ForkJoinSumCalculator(elements, start + length / 2, end);
					Long rightResult = right.compute();
					Long leftResult = left.get();
					return leftResult + rightResult;
				} else {
					return doTheCalc();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		long doTheCalc() {
			return Arrays.stream(elements).skip(start).limit(end - start).sum();
		}
	}

	public static void main(String[] args) {
		int count = 2000;
		long[] elements = LongStream.rangeClosed(1, count).toArray();

		ForkJoinSumCalculator calc = new ForkJoinSumCalculator(elements, 0, count);
		ForkJoinPool pool = new ForkJoinPool();
		long result = pool.invoke(calc);
		System.out.println(result);
		
		System.out.println( Arrays.stream(elements).sum());
	}

}
