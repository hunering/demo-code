package com.xmo.demo.java7.json;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

public class JsonLibTest {

	public static void main(String[] args) {
	    createJson();

	}
	
	public static void createJson() {

	    JSONObject jsonObj = new JSONObject();
	    jsonObj.put("JSON", "Hello, World!");
	    System.out.println(jsonObj);
	}

	public static void ParseJson() {

		String jsonString = "{\"errorMessage\":\"\","
				+ "\"responseCode\":0,"
				+ "\"responseObj\":[{\"name\":\"PROCESSOR_TYPE\",\"value\":\"IMM2 NM30\"}]"
				+ "}";

		JSONObject jb = JSONObject.fromObject(jsonString);
		JSONArray responseObj = jb.getJSONArray("responseObj");
		Iterator<JSONObject> iterator = responseObj.iterator();

		for (; iterator.hasNext();) {
			JSONObject item = iterator.next();
			System.out.println(item.getString("name"));
			System.out.println(item.getString("value"));
			String itemName = item.getString("name");
			String itemValue = item.getString("value");
			if (itemName.equals("PROCESSOR_TYPE")) {

			} else if (itemName.equals("FIRMWARE_VERSION")) {

			} else if (itemName.equals("MODEL")) {

			} else if (itemName.equals("IDENTIFICATION_TOKEN")) {

			} else if (itemName.equals("CAPABILITY")) {

			}
		}

	}

}
