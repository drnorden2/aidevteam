package com.aidevteam.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtReader {
    public List<String> read(String filePath){
        List<String> lines = new ArrayList<>();
        // File path is passed as parameter
        File file = new File(filePath);
        if(file.exists()){

            // Note:  Double backquote is to avoid compiler
            // interpret words
            // like \test as \t (ie. as a escape sequence)

            // Creating an object of BufferedReader class
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                // Declaring a string variable
                String line;
                // Condition holds true till
                // there is character in a string
                while (( line = br.readLine()) != null){
                    lines.add(line);
                }

                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return lines;
    }
}
