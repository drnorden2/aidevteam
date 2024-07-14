package com.aidevteam.prompts;

import java.util.ArrayList;
import java.util.List;

public class Prompt {
    private final String NEW_LINE = "\n";
    private String name;
    private List<String> elements =new ArrayList<>();
    public Prompt derive(){
        return new Prompt(this);
    }
    public Prompt(String name){
        this.name = name;
    }
    private Prompt(Prompt other){
        Prompt newP = new Prompt(other.name);
        newP.elements.addAll(other.elements);
    }
    public void add (String element){
        elements.add(element);
    }
    public String toString(){
        StringBuffer sb = new StringBuffer();
        for(String line:elements){
            sb.append(line);
            if (!line.endsWith(NEW_LINE)){
                sb.append(NEW_LINE);
            }
        }
        return sb.toString();
    }
}
