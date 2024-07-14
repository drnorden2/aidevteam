package com.aidevteam.util;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


public class InlineCompiler {
    private ByteArrayOutputStream tmpOut;
    private ByteArrayOutputStream tmpErr;

    private static final PrintStream origOut = System.out;
    private static final PrintStream origErr = System.err;
    private String snippetClassPath;
    private String appClassPath;
    private int snippetClassCounter=0;
    private static InlineCompiler singleton;

    public static void main(String[] args) {
        {
            StringBuilder sb = new StringBuilder(64);
            sb.append("public class HelloWorld implements Runnable {\n");
            sb.append("    public void run (){\n");
            sb.append("        System.out.println(\"Hello world\");\n");
            sb.append("    }\n");
            sb.append("}\n");
            InlineCompiler inlineCompile = getInlineCompilerSingleton("inline_compile");
            String className = inlineCompile.compileClass(sb.toString());
            String[] outErr = inlineCompile.executeClass( className);

            System.out.println("Executed " + className + ": ");
            System.out.println(outErr[0]);
            if (!outErr[1].isEmpty()) {
                System.out.println("Error:");
                System.out.println(outErr[1]);
            }
        }
        {
            StringBuilder sb = new StringBuilder(64);
            sb.append("System.out.println(\"Hello world\");\n" +
                    "String a =null;\n" +
                    "a.toString();\n");
            InlineCompiler inlineCompile = getInlineCompilerSingleton("inline_compile");
            String className = inlineCompile.compileSnippet(sb.toString());
            String[] outErr = inlineCompile.executeSnippet(className);

            System.out.println("Executed " + className + ": ");
            System.out.println(outErr[0]);
            if (!outErr[1].isEmpty()) {
                System.out.println("Error:");
                System.out.println(outErr[1]);
            }
        }


    }
    public  static synchronized InlineCompiler getInlineCompilerSingleton(String path){
        if(singleton==null){
            File pathFile = new File(path);
            if(!pathFile.exists()){
                pathFile.mkdirs();
            }
            singleton = new InlineCompiler(pathFile.getAbsolutePath());
        }
        return singleton;
    }
    private InlineCompiler(String path){
        this.snippetClassPath = path+"/java_snippets";
        this.appClassPath = path+"/java_app";
    }

    public String compileSnippet(String javaSnippetCode){
        String  className = storeJavaSnippetAndReturnClassName(javaSnippetCode);
        compile(snippetClassPath,className);
        return className;
    }
    public String compileClass(String javaClassCode){
        int indexOfClass = javaClassCode.indexOf(" class ");
        if(indexOfClass==-1){
            throw new RuntimeException("The code does not contain a class definition");
        }

        String className="";
        for(int i= indexOfClass+6;i<javaClassCode.length();i++){
            char c = javaClassCode.charAt(i);
            if(className.isEmpty() && c==' ')continue;
            if(c>='A'&&c<='Z' ||c>='a'&&c<='z'||c>='0'&&c<='9'||c=='-'||c=='_'){
                className+=c;
            }else{
                break;
            }
        }
        storeAsJavaFile(this.appClassPath,className,javaClassCode);
        compile(appClassPath,className);
        return className;
    }


    private String storeJavaSnippetAndReturnClassName(String javaSnippetCode) {
        String className = "SnippetClass"+snippetClassCounter++;
        StringBuilder sb = new StringBuilder(128);
        sb.append("public class ").append(className).append(" implements Runnable {\n");
        sb.append("    public void run() {\n");
        sb.append(javaSnippetCode);
        sb.append("\n    }\n");
        sb.append("}\n");
        storeAsJavaFile(this.snippetClassPath,className,sb.toString());
        return className;
    }



    private File storeAsJavaFile(String classPath, String className, String javaClassCode) {
        File javaFile = new File(classPath + "/" + className + ".java");
        if (javaFile.getParentFile().exists() || javaFile.getParentFile().mkdirs()) {

            try {
                Writer writer = null;
                try {
                    writer = new FileWriter(javaFile);
                    writer.write(javaClassCode);
                    writer.flush();

                } finally {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
        return javaFile;
    }

    private String compile(String classPath,String className){
        File javaFile = new File(classPath + "/" + className + ".java");
        try{
            /** Compilation Requirements *********************************************************************************************/
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            // This sets up the class path that the compiler will use.
            // I've added the .jar file that contains the DoStuff interface within in it...
            List<String> optionList = new ArrayList<String>();
            optionList.add("-classpath");
            optionList.add(System.getProperty("java.class.path") + File.pathSeparator + classPath);

            Iterable<? extends JavaFileObject> compilationUnit
                    = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    optionList,
                    null,
                    compilationUnit);
            /********************************************************************************************* Compilation Requirements **/
            if (!task.call()){
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.out.format("Error on line %d in %s: %s %n",
                            diagnostic.getLineNumber(),
                            diagnostic.getSource().toUri(),
                            diagnostic.getMessage(Locale.US)

                    );
                }
            }
        } catch (Exception  exp) {
            exp.printStackTrace();
        }

        return "";
    }

    public String[] executeClass(String runnableClassName) {
        return execute(appClassPath,runnableClassName);
    }
    public String[] executeSnippet(String snippetClassName) {

        return execute(snippetClassPath,snippetClassName);
    }
    private String[] execute (String classPath, String className) {
            try {
            /** Load and execute *************************************************************************************************/
            // Create a new custom class loader, pointing to the directory that contains the compiled
            // classes, this should point to the top of the package structure!
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(classPath).toURI().toURL()});
            // Load the class from the classloader by name....
            Class<?> loadedClass = classLoader.loadClass(className);
            // Create a new instance...
            Object obj = loadedClass.newInstance();
            // Santity check
            if (obj instanceof Runnable) {
                // Cast to the DoStuff interface
                Runnable runnable = (Runnable) obj;
                // Run it baby
                synchronized (this) {
                    redirectSystemOutErr();
                    runnable.run();
                    String out = this.tmpOut.toString();
                    String err = this.tmpErr.toString();
                    restoreSystemOutErr();
                    return new String[]{out,err};
                }
            }
        }catch(Exception e) {
            restoreSystemOutErr();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new String[]{"","Could not run "+className+". Not a Runnable."};
    }

    private void redirectSystemOutErr() {
        tmpOut = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(tmpOut);
        System.setOut(ps);
        tmpErr = new ByteArrayOutputStream();
        ps = new PrintStream(tmpErr);
        System.setErr(ps);

    }
    private void restoreSystemOutErr(){
            System.setOut(origOut);
            System.setErr(origErr);
    }


}
