package com.aidevteam.util;

import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TxtCache {
    private final Map<String,String> cache = new HashMap<>();
    private PrintWriter out;
    private int idCounter =0;
    public TxtCache(String filePath) {
        List<String> lines = new TxtReader().read(filePath);
        for(String line:lines){
            String[] pair = line.split("\t");
            String key = StringEscapeUtils.unescapeJava(pair[0]);
            String value = StringEscapeUtils.unescapeJava(pair[1]);
            Integer nr = getNrOrNull(value);
            if(nr!=null && idCounter<nr){
                idCounter=nr;
            }
            cache.put(key,value);
        }
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw,true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private Integer getNrOrNull(String str){
        Integer retVal =null;
        try{
            retVal = Integer.parseInt(str);
        }catch (Exception e){
            //silent
        }
        return retVal;
    }
    public synchronized String put(String key, String value){
        String old = cache.put(key,value);
        if(!value.equals(old)){
            persist(key,value);
        }
        return old;
    }
    public synchronized String getIdKey(String key){
        String id = cache.get(key);
        if(id==null) {
            id = "" + ++idCounter;
            cache.put(key, id);
            persist(key, id);
        }
        return id;
    }
    public String get(String key){
        return cache.get(key);
    }
    private void persist(String key, String value){
        key = StringEscapeUtils.escapeJava(key);
        value = StringEscapeUtils.escapeJava(value);
        if(value.trim().isEmpty()){
            value ="-";
        }
        out.println(key+"\t"+value);
    }

    public void exit(){
        out.close();
    }

    public static void main(String[] args) {
        TxtCache cache = new TxtCache("/tmp/cache/cache.txt");
        System.out.println(cache);
        cache.put("Mamma","mina");
        System.out.println(cache);


    }
    public String toString(){
        StringBuffer b = new StringBuffer();
        for(String key:cache.keySet()){
            String value = this.get(key);
            b.append(key);
            b.append(": ");
            b.append(value);
            b.append('\n');
        }
        return b.toString();
    }
}
