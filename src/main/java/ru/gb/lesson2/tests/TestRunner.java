package ru.gb.lesson2.tests;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TestRunner {

    public static void run(Class<?> testClass) {
        final Object testObj = initTestObj(testClass);

        ArrayList<Method> beforeEachMethods = new ArrayList<>();
        ArrayList<Method> afterEachMethods = new ArrayList<>();
        ArrayList<Method> beforeAllMethods = new ArrayList<>();
        ArrayList<Method> afterAllMethods = new ArrayList<>();
        Map<Integer, Method> testMethods = new HashMap<>() {
        };


        for (Method testMethod : testClass.getDeclaredMethods()) {
            if (testMethod.accessFlags().contains(AccessFlag.PRIVATE)) {
                continue;
            }

            if (testMethod.getAnnotation(Test.class) != null)
                testMethods.put(testMethod.getAnnotation(Test.class).order(), testMethod);
            else if (testMethod.getAnnotation(AfterAll.class) != null)
                afterAllMethods.add(testMethod);
            else if (testMethod.getAnnotation(BeforeAll.class) != null)
                beforeAllMethods.add(testMethod);
            else if (testMethod.getAnnotation(AfterEach.class) != null)
                afterEachMethods.add(testMethod);
            else if (testMethod.getAnnotation(BeforeEach.class) != null)
                beforeEachMethods.add(testMethod);
        }

        try {
            for (Method testMethod : beforeAllMethods)
                testMethod.invoke(testObj);

            for (Map.Entry<Integer, Method> it :
                    testMethods.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .toList()) {

                for (Method beforeMethod : beforeEachMethods) beforeMethod.invoke(testObj);

                it.getValue().invoke(testObj);

                for (Method afterMethod : afterEachMethods) {
                    afterMethod.invoke(testObj);
                }
            }

            for (Method testMethod : afterAllMethods)
                testMethod.invoke(testObj);


        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);

        }
    }


    private static Object initTestObj(Class<?> testClass) {
        try {
            Constructor<?> noArgsConstructor = testClass.getConstructor();
            return noArgsConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Нет конструктора по умолчанию");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось создать объект тест класса");
        }
    }
}

