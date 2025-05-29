package com.baidu.api.utils;

import com.baidu.api.model.UserInput;
import com.github.javafaker.Faker;

import java.util.Locale;

public class MockDataUtils {

    private static final Faker faker = new Faker(Locale.CHINA);

    public static UserInput randomUserInput() {
        return new UserInput()
                .setName(faker.name().fullName())
                .setEmail(faker.internet().emailAddress())
                .setAge(faker.number().numberBetween(18, 60));
    }
}
