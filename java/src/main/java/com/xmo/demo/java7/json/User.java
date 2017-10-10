package com.xmo.demo.java7.json;

import java.util.List;

public class User {
    @Override
    public String toString() {
        return "User [name=" + name + ", age=" + age + ", messages=" + messages + "]";
    }
    private String name;
    private int age;
    private List<String> messages;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public List<String> getMessages() {
        return messages;
    }
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
