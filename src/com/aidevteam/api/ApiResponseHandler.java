package com.aidevteam.api;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Arrays;

public class ApiResponseHandler {
    public void printException(StringBuffer sb,String context,Exception e){
        sb.append(context);
        sb.append("\n");
        sb.append(ExceptionUtils.getStackTrace(e));
    }
    public void printSystemOutErr(StringBuffer sb,String context,String[] outErr){
        sb.append(context);
        sb.append("\n");
        if(outErr.length>0 ){
            sb.append(outErr[0]);
            sb.append("\n");
        }
        if(outErr.length>1 && !outErr[1].trim().isEmpty()){
            sb.append("The following error occured:\n");
            sb.append(outErr[1]);
            sb.append("\n");
        }
    }
}
