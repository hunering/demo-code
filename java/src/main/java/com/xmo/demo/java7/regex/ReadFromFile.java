package com.xmo.demo.java7.regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFromFile {

    public static void main(String[] args) throws IOException {
        System.out.println(String.format("%.2f", 1234.5678));
        System.out.println(String.format("%.0f", 1234.5678));
//        System.out.println(String.valueOf(1234.5678));
        String fileName = "regexstring.txt";
        Path filePath = Paths.get(fileName);
        BufferedReader reader = Files
                .newBufferedReader(filePath, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        String content = reader.readLine();
        while (content != null) {
            sb.append(content);
            content = reader.readLine();
        }

        System.out.println(sb.toString());

        // String delimiters = "<div
        // class=\"daojishi\">.*当前交易节开始时间.*(\\d{2}:\\d{2}).*(\\d{2}:\\d{2}).*(\\d{4}-\\d+-\\d+
        // \\d{2}:\\d{2}:\\d{2})</span></strong>";
        // String delimiters = "<input type=\"hidden\" id=\"maxQ\"
        // value=\"(\\d+)\">";
        String delimiters = "<a href=\"http://fengshan.shpgx.com/delivery/order/detail.htm\\?"
                + "id=(\\d+)\".*?>(\\d+)</a>" // id, code
                + ".*?<td>(\\S+)</td>"// CEGS
                + ".*?<td>(\\S+)</td>"// 管道天然气
                + ".*?<td>(\\S+)</td>"// price
                + ".*?<td>(\\d+)立方米</td>"// amount
                + ".*?<td>(\\S+)</td>"// 保证金
                + ".*?<td>(\\S+)</td>"// 交易服务费
                + ".*?<td>(\\S+)</td>"// 交收服务费
                + ".*?<td>(\\S+)</td>"// 货款
                + ".*?<td>(\\S+)</td>"// 交收次数
                + ".*?<td>(.*?)</td>";// 成交时间
        // delimiters = "总条数: (\\d+).*页数: \\[ (\\d+) / (\\d+) \\]";
        delimiters = "<input type=\"hidden\" id=\"highUpRange\" value=\"(\\d+\\.*\\d*)\">";
        delimiters = "<input type=\"hidden\" id=\"highUpRange\" value=\"(\\d+\\.*\\d*)\"/>";
        String str = sb.toString();

        Pattern p = Pattern.compile(delimiters);
        Matcher m = p.matcher(str);
        int total = 0;
        while (m.find()) {
            total++;
            System.out.println(m.group());
            for (int i = 0; i < m.groupCount(); i++) {
                System.out.println(m.group(i + 1));
            }
        }
        System.out.println("total matched: " + total);

    }

}
