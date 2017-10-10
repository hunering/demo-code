package com.xmo.demo.java7.json;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;;

public class JacksonTest {

    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.createObjectNode();
        JsonNode marksNode = mapper.createArrayNode();

        ((ArrayNode) marksNode).add(100);
        ((ArrayNode) marksNode).add(90);
        ((ArrayNode) marksNode).add(85);

        ((ObjectNode) rootNode).put("name", "Mahesh Kumar");
        ((ObjectNode) rootNode).put("age", 21);
        ((ObjectNode) rootNode).put("age", 22);
        ((ObjectNode) rootNode).put("verified", false);
        ((ObjectNode) rootNode).put("marks", marksNode);

        ;
        parseFromString();
        json2JavaObject();

    }
    
    public static void parseFromString() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree("{\"k1\":\"v1\"}");
        System.out.println(actualObj.get("k1"));
    }

    public static void json2JavaObject() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "{\"name\" : \"mkyong\"}";

        //JSON from String to Object
        User user = mapper.readValue(jsonInString, User.class);
        System.out.println(user);
    }
}
