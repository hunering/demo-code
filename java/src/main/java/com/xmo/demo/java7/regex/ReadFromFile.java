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

        
        String delimiters = "<div class=\"daojishi\">.*当前交易节开始时间.*(\\d{2}:\\d{2}).*(\\d{2}:\\d{2}).*(\\d{4}-\\d+-\\d+ \\d{2}:\\d{2}:\\d{2})</span></strong>";
        
        String str = sb.toString();
        
        Pattern p = Pattern.compile(delimiters);
        Matcher m = p.matcher(str);
        while (m.find()) {          
            for(int i=0; i <m.groupCount(); i++) {
                System.out.println(m.group(i+1));
            }
        }
        
    }

}
