package com.aidevteam.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DB {
    private String name;
    private Map<String,Bucket> buckets;
    public DB(String name){
        this.name = name;
        buckets = new HashMap<>();
    }
    public void addBucket(Bucket bucket){
        buckets.put(bucket.getName(),bucket);
    }
    public Set<String> getBucketNames(){
        return buckets.keySet();
    }
    public Bucket getBucket(String name){
        return buckets.get(name);
    }
}
