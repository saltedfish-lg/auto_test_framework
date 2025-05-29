package com.baidu.api.model;

public class UserInput {
    private String name;
    private String email;
    private int age;

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }

    public UserInput setName(String name) {
        this.name = name;
        return this;
    }

    public UserInput setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserInput setAge(int age) {
        this.age = age;
        return this;
    }
}
