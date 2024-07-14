package com.aidevteam.api;

import jdk.jfr.AnnotationElement;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ApiDoc {
    private static final int ACCESS_MODIFIERS = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;


    public static String listAllCommands(Class clazz, ApiCategory[] filters){
        Method[] methods = clazz.getDeclaredMethods();
        StringBuffer sb = new StringBuffer();
        for (Method method: methods) {
            if (!method.isAnnotationPresent(ApiMethodAnnotation.class)) {
                continue;
            }
            ApiMethodAnnotation annotation = method.getAnnotation(ApiMethodAnnotation.class);
            if (!containsFilter(annotation,filters) ) {
                continue;
            }
            printModifiersIfNonzero(sb,method.getModifiers(),method.getModifiers(),method.isDefault());
            sb.append(onlyName(method.getReturnType().getTypeName()));
            sb.append(" ").append(method.getName());
            printParameters(sb,method.getParameterTypes(),annotation.params());
            //list += "public "+  +" " +method.getName() +"("+parameters+");\n" +
            sb.append("\n");
            sb.append(annotation.description()+" \n\n");
            //Class<?>[] exceptionTypes;

        }
        return sb.toString();
    }


    private static void printParameters(StringBuffer sb, Class<?>[]  parameterTypes,String[] annotationParams) {
        /*
        sb.append(Arrays.stream(parameterTypes)
                .map(Type::getTypeName)
                .collect(Collectors.joining(",", "(", ")")));
        String parameters = "";
        */
        boolean first = true;
        sb.append("(");
        for (String param : annotationParams) {
            if(first){
                first = false;
            }else{
                sb.append(", ");
            }

            sb.append(param);
        }
        sb.append(")");
    }
    private static String onlyName(String name){
        int last = name.lastIndexOf('.');
        if(last>0){
            name = name.substring(last+1);
        }
        return name;
    }
    private static boolean containsFilter(ApiMethodAnnotation annotation, ApiCategory[] filters){
        boolean found = false;
        if(Arrays.asList( annotation.category()).contains(ApiCategory.ALL)){
            return true;
        }
        for(ApiCategory filter:filters) {
            if (!Arrays.asList( annotation.category()).contains(filter)) {
                found=true;
                break;
            }
        }
        return found;
    }
    private static void printModifiersIfNonzero(StringBuffer sb,int modifiers, int mask, boolean isDefault) {
        int mod = modifiers & mask;

        if (mod != 0 && !isDefault) {
            sb.append(Modifier.toString(mod)).append(' ');
        } else {
            int access_mod = mod & ACCESS_MODIFIERS;
            if (access_mod != 0)
                sb.append(Modifier.toString(access_mod)).append(' ');
            if (isDefault)
                sb.append("default ");
            mod = (mod & ~ACCESS_MODIFIERS);
            if (mod != 0)
                sb.append(Modifier.toString(mod)).append(' ');
        }
    }
}
