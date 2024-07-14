package com.aidevteam.util;

import java.util.ArrayList;
import java.util.List;

public class EditableFile {
    private final TxtCache txt;
    private final String name;
    private final static String NO_LINES = "LINES";
    private List<String> lines = new ArrayList<>();

    public EditableFile (String name,String filePath){
        this.txt = new TxtCache(filePath);
        String noLinesStr = txt.get(NO_LINES);
        if(lines!=null){
            try{
                int noLines = Integer.parseInt(noLinesStr);
                for(int i=0;i<noLines;i++){
                    lines.add(txt.get(""+i));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        this.name = name;
    }

    public String listFile(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("File: "+name+"\n");
        int i=0;
        for(String line:lines){
            buffer.append (i+++"\t"+line+"\n");
        }
        return buffer.toString();
    }

    public String getName() {
        return name;
    }
    public boolean deleteLineNr(int lineNr){
        boolean valid =false;
        if(lineNr>=0 &&lineNr<lines.size()){
            lines.set(lineNr,null);
            valid =true;
        }
        return valid;
    }
    public boolean replaceLine(int lineNr,String newLines){
        boolean valid =false;
        if(lineNr>=0 &&lineNr<lines.size()){
            lines.set(lineNr,newLines);
            valid =true;
        }
        return valid;
    }
    public boolean addLine(String newLines){
        lines.add(newLines);
        return true;
    }
    public void stopEdit(){
        int noLines =0;
        List<String> newLines =new ArrayList<>();
        for(String line:lines){
            if(line!=null){
                String[] subLines = line.split(System.lineSeparator());
                for(String sLine:subLines){
                    txt.put(""+noLines++,sLine);
                    newLines.add(sLine);
                }
            }
        }
        txt.put(NO_LINES,""+noLines);
        lines = newLines;
    }
    public int getNoLines(){
        return lines.size();
    }

    public static void main(String[] args) {
        EditableFile file = new EditableFile("HelloWorld.java","Test.txt");
        /*
        file.add("public class HelloWorld{");
        file.add("  public static void main(String[] args){");
        file.add("    System.out.println(\"Hello World\");");
        file.add("  }");
        file.add("}");
        */

        file.deleteLineNr(8 );
        file.stopEdit();
        System.out.println(file.listFile());
    }
}
