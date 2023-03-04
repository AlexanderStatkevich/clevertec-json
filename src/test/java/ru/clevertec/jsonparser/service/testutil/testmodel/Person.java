package ru.clevertec.jsonparser.service.testutil.testmodel;

import java.util.Objects;

public class Person {
    private int id;
    private String name;
    private int age;
    private Passport passport;

    public Person() {
    }

    public Person(int id, String name, int age, Passport passport) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.passport = passport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public Passport getPassport() {
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && age == person.age && Objects.equals(name, person.name) && Objects.equals(passport, person.passport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, passport);
    }
}
