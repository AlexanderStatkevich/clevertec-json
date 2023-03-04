package ru.clevertec.jsonparser.service.testutil.testmodel;

import ru.clevertec.jsonparser.service.testutil.testmodel.api.Builder;

import java.util.List;
import java.util.Map;

public class GroupTestBuilder implements Builder<Group> {

    private int id = 1;
    private String title = "Java Learning";
    private List<Person> persons = List.of(
            new Person(1, "Val", 33,
                    new Passport(1, true, "314535252A14PB15", 'q', Map.of(1L, "first"))),
            new Person(2, "Kate", 27,
                    new Passport(2, true, "319347472A14PB26", 'f', Map.of(2L, "second", 3L, "third"))));
    private boolean check = true;

    private GroupTestBuilder() {
    }

    private GroupTestBuilder(GroupTestBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.persons = builder.persons;
        this.check = builder.check;
    }

    public static GroupTestBuilder aGroup() {
        return new GroupTestBuilder();
    }

    public GroupTestBuilder withId(int id) {
        final var copy = new GroupTestBuilder(this);
        copy.id = id;
        return copy;
    }

    public GroupTestBuilder withTitle(String title) {
        final var copy = new GroupTestBuilder(this);
        copy.title = title;
        return copy;
    }

    public GroupTestBuilder withPersons(List<Person> persons) {
        final var copy = new GroupTestBuilder(this);
        copy.persons = persons;
        return copy;
    }

    public GroupTestBuilder withCheck(boolean check) {
        final var copy = new GroupTestBuilder(this);
        copy.check = check;
        return copy;
    }

    @Override
    public Group build() {
        return new Group(id, title, persons, check);
    }
}
