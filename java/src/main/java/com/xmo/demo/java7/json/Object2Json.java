package com.xmo.demo.java7.json;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Object2Json {

	static public class ScanMultipleCase {
		private String host;
		private String username;
		private String password;
		private String identificationToken;
		private String scan_errorMsg;
		private int scan_responseCode;
		private String scan_powserState;
		private String scan_ledState;
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
	};
	
	public static void main(String[] args) {
		ScanMultipleCase caseItem = new Object2Json.ScanMultipleCase();
		caseItem.setHost("Host");
		caseItem.username = "UserName";
		
		List<ScanMultipleCase> cases = new ArrayList<ScanMultipleCase>();
		cases.add(caseItem);
		cases.add(caseItem);
		JSON json = JSONSerializer.toJSON(cases);
		System.out.println(json);
		
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Host", "Host");
		jsonArray.add(jsonObject);
		System.out.println(jsonArray.toString());
		
	}

}
