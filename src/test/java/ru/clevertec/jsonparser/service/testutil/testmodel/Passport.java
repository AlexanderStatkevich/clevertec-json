package ru.clevertec.jsonparser.service.testutil.testmodel;

import java.util.Map;
import java.util.Objects;

public class Passport {
    private int id;
    private boolean valid;
    private String number;
    private char character;
    private Map<Long, String> map;

    public Passport() {
    }

    public Passport(int id, boolean valid, String number, char character, Map<Long, String> map) {
        this.id = id;
        this.valid = valid;
        this.number = number;
        this.character = character;
        this.map = map;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public Map<Long, String> getMap() {
        return map;
    }

    public void setMap(Map<Long, String> map) {
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passport passport = (Passport) o;
        return id == passport.id && valid == passport.valid && character == passport.character && Objects.equals(number, passport.number) && Objects.equals(map, passport.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valid, number, character, map);
    }
}
