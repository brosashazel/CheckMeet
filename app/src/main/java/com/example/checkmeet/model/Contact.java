package com.example.checkmeet.model;

/**
 * Created by Hazel on 19/03/2017.
 */

public class Contact {
    private String name;
    private String number;
    private int color;

    public Contact() {
    }

    public Contact(String name, String number, int color) {
        this.name = name;
        this.number = number;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
