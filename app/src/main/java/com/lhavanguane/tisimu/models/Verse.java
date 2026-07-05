package com.lhavanguane.tisimu.models;

public class Verse {
    private int number;
    private String text;
    private boolean isSelected;

    public Verse(int number, String text) {
        this.number = number;
        this.text = text;
        this.isSelected = false;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}