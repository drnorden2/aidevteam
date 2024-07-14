package com.aidevteam.bots;

import com.aidevteam.api.Api;
import com.aidevteam.prompts.Prompt;

public abstract class AiBot {
    private String name;
    private String systemPrompt;
    public final Api api ;

    public AiBot(String name, Prompt systemPrompt,Api api) {
        this.name = name;
        this.api = api;
        this.systemPrompt = systemPrompt.toString();
    }
    abstract public String request(Prompt prompt);
    public final String getSystemPrompt(){
        return systemPrompt;
    }

}
