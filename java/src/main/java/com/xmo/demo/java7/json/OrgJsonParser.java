package com.xmo.demo.java7.json;

import org.json.*;

public class OrgJsonParser {
	public static void main(String[] args) {
		String jsonString = "{\"errorMessage\":\"\","
				+ "\"responseCode\":0,"
				+ "\"responseObj\":[{\"name\":\"PROCESSOR_TYPE\",\"value\":\"IMM2 NM30\"}]"
				+ "}";

		JSONObject json = new JSONObject(jsonString);

		int responseCode = json.getInt("responseCode");
		// get the data
		JSONArray responseObj = (JSONArray) json.get("responseObj");

		for (int i = 0; i < responseObj.length(); i++) {
			JSONObject item = responseObj.getJSONObject(i);
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
