package com.aidevteam.api;


import javax.xml.catalog.Catalog;
import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;

// needed to make annotation available at runtime
@Inherited
@Retention(RetentionPolicy.RUNTIME)


public @interface ApiMethodAnnotation {
    public String[] params();
    public ApiCategory[] category();
    public String description();


}
