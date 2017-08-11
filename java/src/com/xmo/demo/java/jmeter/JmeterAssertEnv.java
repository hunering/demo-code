package com.xmo.demo.java.jmeter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;

public class JmeterAssertEnv {
    private static final Log log = LogFactory.getLog(JmeterAssertEnv.class);
    private byte[] ResponseData = "response".getBytes();
    static boolean Failure = false;
    static String FailureMessage = "";
    static Map<String, String> vars = new HashMap<String, String>();

    public static void print(String str) {
        System.out.print(str);
    }

    public static void print(Throwable t) {
        System.out.print(t.toString());
    }

    public static void main(String[] args) {
        parseJsonUser();
    }

    public static void parseJsonUser() {
        String responseJson = "{\"errorCode\": \"SUCCEEDED\", \"userId\": \"12\"}";
        JSONObject jb = JSONObject.fromObject(responseJson);

        String errorCode = jb.getString("errorCode");
        if (!"SUCCEEDED".equals(errorCode)) {
            Failure = true;
            FailureMessage = "The errorCode is not SUCCEEDED";
        }
        int userId = jb.getInt("userId");
        if (userId < 0) {
            Failure = true;
            FailureMessage = "The userId is invalid";
        }

    }

    boolean checkResponseCode(String responseCode, String expectedCode) {
        if (responseCode == null) {
            Failure = true;
            FailureMessage = "Response code should not be null.";
            log.error(FailureMessage);
        } else if (responseCode.equals(expectedCode) == false) {
            Failure = true;
            FailureMessage = "Response code expected: " + expectedCode +
                    ", but it is " + responseCode + ".";
            log.error(FailureMessage);
        }

        return !Failure;
    }

    boolean checkUserAddResponse(String response,
            String expectedErroCode, String expectedUserId) {
        JSONObject jb = JSONObject.fromObject(response);

        String errorCode = jb.getString("errorCode");
        if (!expectedErroCode.equals(errorCode)) {
            Failure = true;
            FailureMessage = "The expectedErroCode is: " + expectedErroCode +
                    ", but we got: " + errorCode;
        }
        String userId = jb.getString("userId");
        vars.put("current_user_id", userId);
        if (expectedUserId.equals("positive integer")) {
            long userIdParased = Long.parseLong(userId);
            if (userIdParased < 0) {
                Failure = true;
                FailureMessage = "The userId is expected as: " + expectedUserId
                        + ", but it is: " + userId;
            }
        } else {
            if (!expectedUserId.equals(userId)) {
                Failure = true;
                FailureMessage = "The userId is expected as: " + expectedUserId
                        + ", but it is: " + userId;
            }
        }
        return !Failure;
    }

    String getHttpResponse() {
        String responseJson = null;
        try {
            byte[] arr = (byte[]) ResponseData;
            if (arr != null && arr.length != 0) {
                responseJson = new String(arr);
            } else {
                Failure = true;
                FailureMessage = "The response data size was null";
            }
        } catch (Throwable t) {
            print(t);
            log.warn("Error: ", t);
        }

        return responseJson;
    }

    boolean isJsonFieldEqual(JSONObject j1, JSONObject j2, String field) {
        String j1v = j1.getString(field);
        String j2v = j1.getString(field);
        if (j1v == j2v && j1v == null) {
            return true;
        } else if (j1v != null && j2v != null) {
            return j1v.equals(j2v);
        }
        return false;
    }

    boolean checkUserGetResponse(String response,
            String expectedErroCode, JSONObject expectedUser) {
        JSONObject jb = JSONObject.fromObject(response);

        String errorCode = jb.getString("errorCode");
        if (!expectedErroCode.equals(errorCode)) {
            Failure = true;
            FailureMessage = "The expectedErroCode is: " + expectedErroCode +
                    ", but we got: " + errorCode;
        } else {
            jb.keySet();
            if (isJsonFieldEqual(jb, expectedUser, "photo")
                    && isJsonFieldEqual(jb, expectedUser, "nickname")
                    && isJsonFieldEqual(jb, expectedUser, "wechatId")
                    && isJsonFieldEqual(jb, expectedUser, "wechatName")) {

            } else {
                Failure = true;
                FailureMessage = "The expectedErroCode is: " + expectedErroCode +
                        ", but we got: " + errorCode;
            }
        }
        return !Failure;
    }
}
