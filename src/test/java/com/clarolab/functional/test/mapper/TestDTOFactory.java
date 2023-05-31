/*
 * Copyright (c) 2019, Clarolab. All rights reserved.
 */

package com.clarolab.functional.test.mapper;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestDTOFactory {

    private static Random random = new Random();

    public static <T> T create(Class<T> tClass) throws Exception {
        T instance = tClass.newInstance();
        for (Field field: tClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("id")) {
                field.set(instance, null);
            } else {
                Object value = getRandomValueForField(field);
                field.set(instance, value);
            }
        }
        for (Field field : tClass.getSuperclass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals("id")) {
                field.set(instance, null);
            } else {
                Object value = getRandomValueForField(field);
                field.set(instance, value);
            }
        }
        return instance;
    }

    private static Object getRandomValueForField(Field field) throws Exception {
        Class<?> type = field.getType();
        if(type.isEnum()) {
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        } else if(type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return random.nextInt();
        } else if(type.equals(Long.TYPE) || type.equals(Long.class)) {
            return random.nextLong();
        } else if(type.equals(Double.TYPE) || type.equals(Double.class)) {
            return random.nextDouble();
        } else if(type.equals(Float.TYPE) || type.equals(Float.class)) {
            return random.nextFloat();
        } else if(type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if(type.equals(String.class)) {
            return UUID.randomUUID().toString();
        } else if(type.toString().contains("[Ljava.lang.String;")) {
            String[] answer = {UUID.randomUUID().toString()};
            return answer;
        } else if(type.equals(BigInteger.class)){
            return BigInteger.valueOf(random.nextInt());
        } else if(type.equals(List.class)) {
            return new ArrayList<>();
        }
        return create(type);
    }


}
