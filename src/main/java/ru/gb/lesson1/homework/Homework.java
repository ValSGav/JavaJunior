package ru.gb.lesson1.homework;

import ru.gb.lesson1.Streams;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Homework {


    public static void main(String[] args) {
        Random rnd = new Random();
        String[] departamentsNames = {"Pluto", "Saturn", "Neptune", "Earth", "Moon"};
        String[] personNames = {"Ivan", "Alex", "Bobbie", "Sam", "Din"};
        int sizeOfListPersons = 100;

        List<Streams.Person> persons = Stream.generate(() -> new Streams.Person(
                        personNames[rnd.nextInt(departamentsNames.length)]
                        , rnd.nextInt(16, 70)
                        , rnd.nextInt(30_000, 100_001) * 1.0
                        , new Streams.Department(departamentsNames[rnd.nextInt(departamentsNames.length)])))
                .limit(sizeOfListPersons)
                .toList();

        printNamesOrdered(persons);
        System.out.println(printDepartmentOldestPerson(persons));
        System.out.println(findFirstPersons(persons));
        System.out.println(findTopDepartment(persons));
    }
    /**
     * Реалзиовать методы, описанные ниже:
     * Вывести на консоль отсортированные (по алфавиту) имена персонов
     */
    public static void printNamesOrdered(List<Streams.Person> persons) {
        persons.stream()
                .sorted(Comparator.comparing(Streams.Person::getName))
                .forEach(System.out::println);
    }

    /**
     * В каждом департаменте найти самого взрослого сотрудника.
     * Вывести на консоль мапипнг department -> personName
     * Map<Department, Person>
     */
    public static Map<Streams.Department, Streams.Person> printDepartmentOldestPerson(List<Streams.Person> persons) {
        return persons.stream()
                .collect(Collectors.groupingBy(Streams.Person::getDepartment
                        , Collectors.maxBy(Comparator.comparing(Streams.Person::getAge))))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey
                                            , v -> v.getValue().get()));
    }

    /**
     * Найти 10 первых сотрудников, младше 30 лет, у которых зарплата выше 50_000
     */
    public static List<Streams.Person> findFirstPersons(List<Streams.Person> persons) {
        int limitOfPersonsCount = 10;
        return persons.stream()
                .filter(it -> it.getSalary() > 50_000)
                .filter(it -> it.getAge() < 30)
                .limit(limitOfPersonsCount)
                .collect(Collectors.toList());
    }

    /**
     * Найти депаратмент, чья суммарная зарплата всех сотрудников максимальна
     */
    public static Optional<Streams.Department> findTopDepartment(List<Streams.Person> persons) {
        return Optional.ofNullable(persons.stream()
                .collect(Collectors.groupingBy(Streams.Person::getDepartment
                        , Collectors.summingDouble(Streams.Person::getSalary)))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry<Streams.Department, Double>::getValue))
                .get()
                .getKey());

    }

}

