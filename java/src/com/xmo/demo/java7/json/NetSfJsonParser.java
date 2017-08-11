package com.xmo.demo.java7.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

public class NetSfJsonParser {

	public static void main(String[] args) {
		
		String json = args[0];
		Path jsonFile = Paths.get("json.txt");
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(
				jsonFile, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				try{
					JsonConfig jsonconfig = new JsonConfig();
//					jsonconfig.setRootClass (getRequestObjClass (op));
				
					JSONObject jsonObject =
						(JSONObject)JSONSerializer.toJSON(line, jsonconfig);

					Object obj = JSONSerializer.toJava(jsonObject, jsonconfig);
					if (obj == null)
					{
					}

				}catch(Exception e ){

				}
				
			}
			
		} catch (IOException x) {

		}
		

	}

}
