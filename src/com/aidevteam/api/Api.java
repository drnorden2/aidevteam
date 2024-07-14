package com.aidevteam.api;

import com.aidevteam.util.InlineCompiler;
import jdk.jfr.AnnotationElement;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Api {
    private static Api api ;
    private InlineCompiler inlineCompiler;
    private ApiResponseHandler responseHandler =new ApiResponseHandler();
    public static final Api getApi(String projectPath){
        if(api == null){
            api = new Api(projectPath);
        }
        return api;
    }
    private Api(String projectPath){
        inlineCompiler = InlineCompiler.getInlineCompilerSingleton(projectPath+"/inline_compile/");
    }
    @ApiMethodAnnotation(
            category = ApiCategory.COMPILE,
            description = "Compiles a class code provided and returns the class name or an error message in case something went wrong.",
            params ={"String javaClassCode"})
    public String compileClass(String javaClassCode){
        StringBuffer sb = new StringBuffer();
        try {
            return inlineCompiler.compileClass(javaClassCode);
        }catch (Exception e){
            responseHandler.printException(sb,"An error occured during compilation of the class:",e);

        }
        return sb.toString();
    }
    @ApiMethodAnnotation(category = ApiCategory.COMPILE,
            description = "Compiles snippet code provided and returns the class name or an error message in case something went wrong.",
            params ={"String javaSnippetCode"})
    public String compileSnippet(String javaSnippetCode){
        StringBuffer sb = new StringBuffer();
        try{
            sb.append(inlineCompiler.compileSnippet(javaSnippetCode));
        }catch (Exception e){
            responseHandler.printException(sb,"An error occured during compilation of the snippet:",e);
        }
        return sb.toString();
    }

    @ApiMethodAnnotation(
            category = ApiCategory.EXECUTE,
            description = "Executes a class that implements Runnable and returns System output of the execution.",
            params ={"String runnableClassName"})
    public String executeClass(String runnableClassName) {
        StringBuffer sb = new StringBuffer();
        try{
            responseHandler.printSystemOutErr(sb,"Executing: "+runnableClassName, inlineCompiler.executeClass(runnableClassName));
        }catch (Exception e){
            responseHandler.printException(sb,"An error occured during execution of the snippet:",e);
        }
        return sb.toString();
    }
 
    @ApiMethodAnnotation(
            category = ApiCategory.EXECUTE,
            description = "Executes a class for a snippet and returns System output andErrors of the execution.",
            params ={"String snippetClassName"})
    public String executeSnippet(String snippetClassName) {
        StringBuffer sb = new StringBuffer();
        try{
            responseHandler.printSystemOutErr(sb,"Executing snippet class: "+snippetClassName,inlineCompiler.executeSnippet(snippetClassName));
        }catch (Exception e){
            responseHandler.printException(sb,"An error occured during compilation of the snippet:",e);
        }
        return sb.toString();
    }
    @ApiMethodAnnotation(
            category = ApiCategory.ALL,
            description = "Lists all commands available via API.",
            params = {})
    public static String listAllCommands() {
        ApiCategory[] filters ={ApiCategory.COMPILE,ApiCategory.EXECUTE};
        return ApiDoc.listAllCommands(Api.class,filters);
    }



}
