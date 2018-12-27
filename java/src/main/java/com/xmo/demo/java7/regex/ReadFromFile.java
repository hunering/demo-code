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
            sb.append("\n");
            content = reader.readLine();
        }

        System.out.println(sb.toString());

        // String delimiters = "<div
        // class=\"daojishi\">.*å½“å‰�äº¤æ˜“èŠ‚å¼€å§‹æ—¶é—´.*(\\d{2}:\\d{2}).*(\\d{2}:\\d{2}).*(\\d{4}-\\d+-\\d+
        // \\d{2}:\\d{2}:\\d{2})</span></strong>";
        // String delimiters = "<input type=\"hidden\" id=\"maxQ\"
        // value=\"(\\d+)\">";
        String delimiters = "<a href=\"http://fengshan.shpgx.com/delivery/order/detail.htm\\?"
                + "id=(\\d+)\".*?>(\\d+)</a>" // id, code
                + ".*?<td>(\\S+)</td>"// CEGS
                + ".*?<td>(\\S+)</td>"// ç®¡é�“å¤©ç„¶æ°”
                + ".*?<td>(\\S+)</td>"// price
                + ".*?<td>(\\d+)ç«‹æ–¹ç±³</td>"// amount
                + ".*?<td>(\\S+)</td>"// ä¿�è¯�é‡‘
                + ".*?<td>(\\S+)</td>"// äº¤æ˜“æœ�åŠ¡è´¹
                + ".*?<td>(\\S+)</td>"// äº¤æ”¶æœ�åŠ¡è´¹
                + ".*?<td>(\\S+)</td>"// è´§æ¬¾
                + ".*?<td>(\\S+)</td>"// äº¤æ”¶æ¬¡æ•°
                + ".*?<td>(.*?)</td>";// æˆ�äº¤æ—¶é—´
        // delimiters = "æ€»æ�¡æ•°: (\\d+).*é¡µæ•°: \\[ (\\d+) / (\\d+) \\]";
        delimiters = "<input type=\"hidden\" id=\"highUpRange\" value=\"(\\d+\\.*\\d*)\">";
        delimiters = "<input type=\"hidden\" id=\"highUpRange\" value=\"(\\d+\\.*\\d*)\"/>";
        delimiters = "(?<=updates Supported : ).+(?=\n)";
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
