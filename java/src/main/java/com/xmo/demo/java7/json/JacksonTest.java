package com.xmo.demo.java7.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;;

public class JacksonTest {

    public static void main(String[] args) {

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

    }

}
