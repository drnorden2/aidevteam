package com.aidevteam.core;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
public class LlamaCppOnMetal implements Askable{
    private BufferedReader reader;
    private LlamaModel model;
    InferenceParameters inferParams;

    public LlamaCppOnMetal(String modelPath){
        LlamaModel.setLogger((level, message) -> System.out.print(message));
        ModelParameters modelParams = new ModelParameters.Builder()
                .setNGpuLayers(1)
                .setNCtx(4096)
                .setNGpuLayers(10000)
                .setSeed(1697568737)
                .setNThreads(10)
                .build();
        inferParams = new InferenceParameters.Builder()
                .setTemperature(0.7f)
                .setPenalizeNl(true)
                .setMirostat(InferenceParameters.MiroStat.V2)
                //.setAntiPrompt(new String[]{"\n"})
                .build();
         model = new LlamaModel(modelPath, modelParams);


    }
    public String ask (String systemPrompt,String question, String parameter){
        String input = question+"\n"+parameter;
        //System.out.print(systemPrompt);
        String prompt = systemPrompt;
        String answer = "";
        prompt += "\nUser: "+input;
        //System.out.println("\nUser: "+input);
        prompt += input;
        //System.out.println("Llama: ");
        prompt += "\nLlama: ";
        for (String output:model.generate(prompt, inferParams)) {
            //System.out.print(output);
            prompt += output;
            answer +=output;
        }
        //System.out.println();
        return  answer;
    }
}
