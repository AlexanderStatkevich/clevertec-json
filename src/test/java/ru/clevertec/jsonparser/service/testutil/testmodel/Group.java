package ru.clevertec.jsonparser.service.testutil.testmodel;

import java.util.List;
import java.util.Objects;

public class Group {
    private int id;
    private String title;
    private List<Person> persons;
    private boolean check;

    public Group() {
    }

    public Group(int id, String title, List<Person> persons, boolean check) {
        this.id = id;
        this.title = title;
        this.persons = persons;
        this.check = check;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id && check == group.check && Objects.equals(title, group.title) && Objects.equals(persons, group.persons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, persons, check);
    }
}
