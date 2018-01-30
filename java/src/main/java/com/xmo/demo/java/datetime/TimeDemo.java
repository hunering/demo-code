package com.xmo.demo.java.datetime;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public class TimeDemo {

    public static void main(String[] args) {
        testDateDefaultFormat();
    }

    public static void testDateDefaultFormat() {
        Date date = new Date();
        // Converts this Date object to a String of the form:
        // dow mon dd hh:mm:ss zzz yyyy
        System.out.println(date.toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
        LocalDateTime time = LocalDateTime.parse("Mon Jan 29 10:47:42 CST 2018", formatter);
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = time.atZone(zoneId).toEpochSecond();
        System.out.println("epoch is " + epoch);

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println(time.format(formatter2));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        System.out.println(time.format(dateTimeFormatter));

        LocalDateTime now = LocalDateTime.now();
        // System.out.println(now.format(formatter));
        long nowEpoch = now.atZone(zoneId).toEpochSecond();
        System.out.println("now epoch is " + nowEpoch);
    }

}
