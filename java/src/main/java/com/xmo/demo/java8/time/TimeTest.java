package com.xmo.demo.java8.time;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.TemporalAdjusters.*;

public class TimeTest {

	public static void main(String[] args) {
		LocalDateTest();
		LocalTimeTest();
		LocalDateTimeTest();

	}

	static void LocalDateTest() {
		LocalDate date = LocalDate.of(2017, 9, 21);
		int year = date.getYear();
		Month month = date.getMonth();
		int day = date.getDayOfMonth();
		DayOfWeek dow = date.getDayOfWeek();
		int len = date.lengthOfMonth();
		boolean leap = date.isLeapYear();
		
		LocalDate now = LocalDate.now();
		int year2 = date.get(ChronoField.YEAR);
		int month2 = date.get(ChronoField.MONTH_OF_YEAR);
		int day2 = date.get(ChronoField.DAY_OF_MONTH);
	}
	
	static void LocalTimeTest() {
		LocalTime time = LocalTime.of(13, 45, 20); //13:45:20
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();
	}
	
	static void LocalDateTimeTest() {
		// 2017-09-21T13:45:20
		LocalDate date = LocalDate.parse("2017-09-21");
		LocalTime time = LocalTime.parse("13:45:20");
		LocalDateTime dt1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
		LocalDateTime dt2 = LocalDateTime.of(date, time);
		LocalDateTime dt3 = date.atTime(13, 45, 20);
		LocalDateTime dt4 = date.atTime(time);
		LocalDateTime dt5 = time.atDate(date);
		
		System.out.println("dt1:" + dt1);
		System.out.println("dt2:" + dt2);
		System.out.println("dt3:" + dt3);
		System.out.println("dt4:" + dt4);
		System.out.println("dt5:" + dt5);
		
		LocalDate date1 = dt1.toLocalDate();
		LocalTime time1 = dt1.toLocalTime();
	}
	
	static void InstantTest() {
		// the following invocations of the ofEpochSecond factory method return	exactly the same Instant :
		Instant.ofEpochSecond(3);
		Instant.ofEpochSecond(3, 0);
		Instant.ofEpochSecond(2, 1_000_000_000);
		Instant.ofEpochSecond(4, -1_000_000_000);
	}
	
	static void DurationTest() {
		LocalTime time1 = LocalTime.of(13, 45, 20);
		LocalTime time2 = LocalTime.of(13, 45, 20);
		Duration d1 = Duration.between(time1, time2);
		
		LocalDateTime dateTime1 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
		LocalDateTime dateTime2 = LocalDateTime.of(2017, Month.SEPTEMBER, 21, 13, 45, 20);
		Duration d2 = Duration.between(dateTime1, dateTime2);
		
		Instant instant1 = Instant.ofEpochSecond(3);
		Instant instant2 = Instant.ofEpochSecond(2);
		Duration d3 = Duration.between(instant1, instant2);
		
		Duration threeMinutes1 = Duration.ofMinutes(3);
		Duration threeMinutes2 = Duration.of(3, ChronoUnit.MINUTES);
	}
	
	static void PeriodTest() {
		Period tenDays = Period.between(LocalDate.of(2017, 9, 11),
				LocalDate.of(2017, 9, 21));
		
		Period tenDays2 = Period.ofDays(10);
		Period threeWeeks = Period.ofWeeks(3);
		Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
	}
	
	static void OperationTest() {
		LocalDate date1 = LocalDate.of(2017, 9, 21);
		LocalDate date2 = date1.withYear(2011);
		LocalDate date3 = date2.withDayOfMonth(25);
		LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);
		
		date1.plusWeeks(1);
		date1.plus(1, ChronoUnit.DAYS);
	}
	
	static void TemporalAdujstsTest() {
		LocalDate date1 = LocalDate.of(2014, 3, 18);
		LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY));//2014-03-23
		LocalDate date3 = date2.with(lastDayOfMonth());//2014-03-31
	}
	
	static void FormatTest() {
		LocalDate date = LocalDate.of(2014, 3, 18);
		String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
		String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate date1 = LocalDate.of(2014, 3, 18);
		String formattedDate = date1.format(formatter);
		LocalDate date2 = LocalDate.parse(formattedDate, formatter);
	}
}
