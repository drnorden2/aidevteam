package com.aidevteam.bots;

import com.aidevteam.api.Api;
import com.aidevteam.core.LlamaCppOnMetal;
import com.aidevteam.core.PersistedLLM;
import com.aidevteam.prompts.Prompt;

import java.io.File;

public class JavaCompileAndExecutorBot extends AiBot{
    private static final Prompt SYSTEM_PROMPT;
    static{
        SYSTEM_PROMPT = new Prompt("JavaCompileAndExecutorBots system prompt");
        SYSTEM_PROMPT.add("This is a conversation a user and Llama, a friendly java compilation and execution bot.\n" +
                "Llama is helpful, kind, honest, writes excellent in compiling and executing java code and never fails to work efficient with its internal API.\n" +
                "Llama compiles and executes corresponding requests immediately and with precision.\n" +
                "Llama will generate the right java method calls for any request to using its internal API:\";\n"+Api.listAllCommands()+"\n" +
                "Llama will always provide a relevant and precises answer also it is only an AI Language model and cannot interact with code.\n");
    }
    PersistedLLM llm;

    public JavaCompileAndExecutorBot(Api api){
        super("JavaCompileAndExecuteBot",SYSTEM_PROMPT, api);

        llm = new PersistedLLM(new LlamaCppOnMetal("/Users/drnorden/projects/models/phind-codellama-34b-v2.Q4_K_M.gguf"/*mistral-7b-instruct-v0.1.Q5_K_S.gguf*/),"/tmp/cache/");

    }


    @Override
    public String request(Prompt prompt) {
        return llm.ask(this.getSystemPrompt(),prompt.toString(),"");
    }

    public static void main(String[] args) {
        Api api = Api.getApi(System. getProperty("user.dir"));
        JavaCompileAndExecutorBot bot = new JavaCompileAndExecutorBot(api);
        //Prompt listApi = new Prompt("ListApi");
        //listApi.add("What options does the API offer?");
        //System.out.println(bot.request(listApi));
        Prompt extract = new Prompt("ListApi");
        extract.add("Extract all valid java statements from below text and list them so that they can be inserted as is into a java program." +
                "Only java statements, no explanations of yours or line numbers or any other comments that would not compile:" +
                "```java\n" +
                "for (int i = 6; i <= 100; i++) {\n" +
                "    boolean isPrime = true;\n" +
                "    for (int j = 2; j * j <= i; j++) {\n" +
                "        if (i % j == 0) {\n" +
                "            isPrime = false;\n" +
                "            break;\n" +
                "        }\n" +
                "    }\n" +
                "    if (isPrime) {\n" +
                "        System.out.println(i);\n" +
                "    }\n" +
                "}\n" +
                "```");

        String answer = bot.request(extract);
        String className = api.compileSnippet(answer);
        if(className.startsWith("SnippetClass")){
            System.out.println("Successfully compiled "+className);
            String response = api.executeSnippet(className);
            System.out.println(response);
        }else{
            extract.add(answer);
            extract.add(className);//Which now is a response
            extract.add("Can you please consider the above problem and try again?");
            answer = bot.request(extract);
            System.out.println(answer);
        }
    }
}
