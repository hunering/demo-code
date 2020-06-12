package com.xmo.demo.java8.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.ArrayList;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.Comparator.comparing;

public class StreamTest {

	public static void main(String[] args) {
		List<Dish> menu = Arrays.asList(
				new Dish("pork", false, 800, Dish.Type.MEAT),
				new Dish("beef", false, 700, Dish.Type.MEAT),
				new Dish("chicken", false, 400, Dish.Type.MEAT),
				new Dish("french fries", true, 530, Dish.Type.OTHER),
				new Dish("rice", true, 350, Dish.Type.OTHER),
				new Dish("season fruit", true, 120, Dish.Type.OTHER),
				new Dish("pizza", true, 550, Dish.Type.OTHER),
				new Dish("prawns", false, 300, Dish.Type.FISH),
				new Dish("salmon", false, 450, Dish.Type.FISH) );
		
		StreamTest test = new StreamTest();
		//test.streamGroupingBy(menu);
		//test.streamMap(menu);
		test.combization();
	}

	public void streamGroupingBy(List<Dish> source) {
		Map<Dish.Type, List<Dish>> result = source.stream().filter((Dish dish)-> dish.getCalories() < 500).collect(groupingBy(Dish::getType));
		
		result.forEach((type, list)->System.out.println(type));
	}
	
	public void streamMap(List<Dish> source) {
		List<String> result = source.stream().map(Dish::getName).sorted().limit(8).collect(toList());
		result.stream().forEach((item)->System.out.println(item));
	}
	
	public void combization() {
		List<Integer> a = Arrays.asList(1,2,3);
		List<Integer> b = Arrays.asList(4,5);
		a.stream().flatMap((i)-> {
			return b.stream().map((j)-> {
				return new Integer[] {i, j};
			});
		}).forEach(System.out::println);
	}
}
