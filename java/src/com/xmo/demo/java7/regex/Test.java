package com.xmo.demo.java7.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
		
//		String IPADDRESS_PATTERN = 
//		        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
//
//		String Ip = null;
//		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
//		Matcher matcher = pattern.matcher("https://192.168.201.101:443");
//        if (matcher.find()) {
//        	Ip = matcher.group();
//        }
		        
		String delimiters = "((?m)^\\d+\\.*\\d+)";
		
		String str = "1.open browser, and navigate to https://host:8743/VirtualKvmGatewayConsole\n"
				+"1.1 1.1step\n"
				+ "2input the administrator username and password\n"
				+ "3.click Login";
		
		Pattern p = Pattern.compile(delimiters);
		Matcher m = p.matcher(str);
		while (m.find()) {			
			for(int i=0; i <m.groupCount(); i++) {
				System.out.println(m.groupCount());
				System.out.println(m.group(i+1));
//				String target = m.group(i+1);
//				String charactor = m.group(i+1).substring(2);
//				int ichar = Integer.parseInt(charactor,16);
//				char thisChar = (char) (ichar&0xff);
//				String to = new String();
//				to += thisChar;
//				System.out.println("user%040&040123".replaceAll(target, to));
			}
		}
		
//		Pattern p = Pattern.compile("^[a-zA-Z]+([0-9]+).*");
//		Matcher m = p.matcher("Testing123Testing");

//		if (m.find()) {
//			System.out.println(m.group(1));
//		}
//		
//		p = Pattern.compile("^WEBVAR_PASSWORD=(.*)&WEBVAR_USERNAME=(.*)&WEBVAR_TYPE=Https");
//		m = p.matcher("WEBVAR_PASSWORD=Password@1&WEBVAR_USERNAME=dcmscan&WEBVAR_TYPE=Https");
//
//		if (m.find()) {
//			System.out.println(m.group(1));
//		}
		
		String regex = "\"user_login\":(?<username>.*),\"password\":(?<password>.*).*";
		String target = "{\"method\":\"login\",\"user_login\":\"testkvm\",\"password\":\"testkvm!\"}";
		
		regex = ".*GetAssertedEvents.*";
		target = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:wsman=\"http://schemas.dmtf.org/wbem/wsman/1/wsman.xsd\">\n"
				+"<SOAP-ENV:Header>"
				+"<wsa:To>https://192.168.110.22:443wsman</wsa:To>"
				+"<wsa:ReplyTo>"
				+"<wsa:Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</wsa:Address>"
				
				+"</wsa:ReplyTo>"
				+"<wsman:ResourceURI>http://www.ibm.com/iBMC/sp/Monitors</wsman:ResourceURI>\n"
				+"<wsa:Action>http://www.ibm.com/iBMC/sp/Monitors/GetAssertedEvents</wsa:Action>\n"
				+"<wsa:MessageID>dt:1432084534662</wsa:MessageID>"
				+" </SOAP-ENV:Header>"
				+"<SOAP-ENV:Body>"
				+"<GetAssertedEvents xmlns=\"http://www.ibm.com/iBMC/sp/Monitors\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"></GetAssertedEvents>"
				+"</SOAP-ENV:Body>"
				+"</SOAP-ENV:Envelope>";	    

		System.out.println("Regex : " + regex);
		System.out.println("Target: " + target);
		
		p = Pattern.compile(regex);
		m = p.matcher(target);
		if (m.matches()) {
//			System.out.println(m.group(0));
//			System.out.println(m.group(1));
//			System.out.println(m.group(2));
			System.out.println("Mached");
		}
		
		
//		String regex = "^name=(?<username>.*),&pwd=(?<password>.*).*";
//		String target = "name=123,&pwd=345";
//		p = Pattern.compile(regex);
//		m = p.matcher(target);
//		if (m.find()) {
//			System.out.println(m.group(1));
//		}
	}
}
