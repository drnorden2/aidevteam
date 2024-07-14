package com.aidevteam.core;

import com.aidevteam.util.TxtCache;

public class PersistedLLM implements Askable{
    private Askable llm;
    private TxtCache systemPrompts;
    private TxtCache questions;
    private TxtCache responses;


    public PersistedLLM(Askable llm,String cachePath){
        this.systemPrompts = new TxtCache(cachePath+"/systemPrompts");
        this.questions = new TxtCache(cachePath+"/questions");
        this.responses = new TxtCache(cachePath+"/responses");
        this.llm = llm;
    }


    @Override
    public String ask(String systemPrompt, String question, String parameter) {
       System.out.println("***********************************************");
       System.out.println(systemPrompt);
        System.out.println("***********************************************");

        System.out.println(question +"\n"+parameter);
        String systemPromptId = systemPrompts.getIdKey(systemPrompt);
        String questionId = questions.getIdKey(question);
        String key = systemPromptId+"|"+questionId+"|"+parameter;
        String response = responses.get(key);
        if(response==null) {
            response = llm.ask(systemPrompt, question, parameter);
            responses.put(key, response);
        }
        System.out.println(response);
        System.out.println("\n\n\n");

        return response;
    }
}
