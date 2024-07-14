package com.aidevteam.db;

public class Entry {
    private int id;
    private static int idCounter=0;
    private String txt;
    public Entry(String txt) {
        this.id = idCounter++;
        this.txt = txt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
