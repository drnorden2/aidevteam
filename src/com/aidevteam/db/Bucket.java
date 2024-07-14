package com.aidevteam.db;

import java.util.HashMap;
import java.util.Map;

public class Bucket {
    private String name;
    private Map<Integer,Entry> entries=new HashMap<>();
    public Bucket (String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addEntry(Entry entry){
        entries.put(entry.getId(),entry);
    }
}
