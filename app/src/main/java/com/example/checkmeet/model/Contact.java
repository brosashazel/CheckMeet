package com.example.checkmeet.model;

/**
 * Created by Hazel on 19/03/2017.
 */

public class Contact {
    private String contactID;
    private String name;
    private String number;
    private int color;

    private boolean selected;

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

    public String getContactID() {
        return contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
