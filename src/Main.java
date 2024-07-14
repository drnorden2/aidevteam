import com.aidevteam.util.EditableFile;
import com.aidevteam.util.TxtReader;
import com.aidevteam.core.LlamaCppOnMetal;
import com.aidevteam.core.PersistedLLM;
import com.aidevteam.db.Bucket;
import com.aidevteam.db.DB;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String... args) {
        //PersistedLLM llm = new PersistedLLM(new LlamaCppOnMetal("/Users/drnorden/projects/models/phind-codellama-34b-v2.Q4_K_M.gguf"/*mistral-7b-instruct-v0.1.Q5_K_S.gguf*/),"/tmp/cache/");
        PersistedLLM llm = new PersistedLLM(new LlamaCppOnMetal("/Users/drnorden/projects/models/dolphin-2.5-mixtral-8x7b.Q5_K_M.gguf"/*mistral-7b-instruct-v0.1.Q5_K_S.gguf*/),"/tmp/cache/");

        String system = "This is a conversation a user and Llama, a friendly senior java developer bot.\n" +
                "Llama is helpful, kind, honest, writes excellent java code and never fails to write code for any " +
                "requests immediately and with precision.\n";

        String question = "[INST]" +
                "Write a java code block without method body that will print prime numbers until 100." +
                "Only the code, that can be then taken later to be pasted int to a main method.";
        //String question = "Analyze the german Text. Please list all words that you do not know as a list of bullets. Make a proposal for a substitute word that you know and would fitd. Ask questions you still have in the third section: ";


        String parameter ="[/INST]";
        //raw german input => structure it
        llm.ask(system, question , parameter );

    }


    public static void mainX(String... args) {
        PersistedLLM llm = new PersistedLLM(new LlamaCppOnMetal("/Users/drnorden/projects/models/phind-codellama-34b-v2.Q4_K_M.gguf"),"/tmp/cache/");

        String system = "This is a conversation a user and Llama, a friendly professional text structuring bot.\n" +
                "Llama is helpful, kind, honest, good at writing, and never fails to structure any " +
                "requests immediately and with precision.\n";

        String question = "Write each scentence as a line of a bullet list with dashes. Use the first word as Headline for the section of bullets. Only use indentations if there is more that one subsequent structual element: ";
        //String question = "Analyze the german Text. Please list all words that you do not know as a list of bullets. Make a proposal for a substitute word that you know and would fitd. Ask questions you still have in the third section: ";


        DB db = new DB("CowDeal");
        Bucket inputLevel = new Bucket("InputLevel");
        db.addBucket(inputLevel);
        TxtReader txtReader = new TxtReader();

        //raw german input => structure it
        List<String> inputs = txtReader.read("/Users/drnorden/projects/java/aidevteam/res/kuhhandel.txt");
        List<String> responses = new ArrayList<>();
        for(String line:inputs){
            if(!line.trim().isEmpty()) {
                String response = llm.ask(system, question , line );
                responses.add(response);
            }
        }
        //structured german input =>translate
        inputs = responses;
        system = "This is a conversation a user and Llama, a friendly professional english german translation bot.\n" +
                "Llama is helpful, kind, honest, good at writing, and never fails to translate any " +
                "requests immediately and with precision.\n";

        question = "Translate the german text to english and preserve the structure of the text.";
        responses = new ArrayList<>();
        for(String line:inputs){
            if(!line.trim().isEmpty()) {
                String response = llm.ask(system, question , line);
                responses.add(response);
            }
        }
        //structured english input => requirements
        inputs = responses;
        system = "This is a conversation a user and Llama, a friendly professional requirements engineering bot.\n" +
                "Llama is helpful, kind, honest, good at writing, and never fails to derive requirements ";

        question = "Derive a set of requirements from below text that can serve as starting point for an implementation of the game.";
        responses = new ArrayList<>();
        for(String line:inputs){
            if(!line.trim().isEmpty()) {
                String response = llm.ask(system, question , line);
                responses.add(response);
            }
        }

        inputs = responses;
        system = "This is a conversation a user and Llama, a friendly professional data processing bot.\n" +
                "Llama is can  store text data in a data store by calling void store(String sumamry, String description) ";
        question = "Output a Call store function call in java style (just the calls, no additional code) for each bullet that including subordinate lines of below input. Derive an at most 3 word summary from the bullet as summary parameter. Use the entire bullet text as description but remove the numbering:";
        responses = new ArrayList<>();
        for(String line:inputs){
            if(!line.trim().isEmpty()) {
                String response = llm.ask(system, question , line);
                responses.add(response);
            }
        }
        EditableFile requirements  = new EditableFile("Requirements","requirements.txt");
        for(String response:responses){
            if(response.startsWith("store(")){

            }
        }

    }
}
