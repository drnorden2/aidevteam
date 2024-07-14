import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;

public class MainOld {
    /*
    translate to english and derive requirements in the form of userstories but only stick to the input provided. explain in the last section how your userstories relate to the input. do not add any additional requirements: Spielziel Jeder Spieler versucht, möglichst viele und möglichst wertvolle Tierquartette zu ersteigern. Es gewinnt, wer am Ende die meisten Punkte hat. Geld hingegen ist am Ende nichts mehr wert.
     */

    public static void main(String... args) throws IOException {
        LlamaModel.setLogger((level, message) -> System.out.print(message));
        ModelParameters modelParams = new ModelParameters.Builder()
                .setNGpuLayers(43)
                .setNCtx(4096)
                .setSeed(1697568737)
                .setNThreads(10)

                .build();
        InferenceParameters inferParams = new InferenceParameters.Builder()
                .setTemperature(0.7f)
                .setPenalizeNl(true)
                .setMirostat(InferenceParameters.MiroStat.V2)
                //.setAntiPrompt(new String[]{"\n"})
                .build();




        String modelPath = "/Users/drnorden/projects/models/phind-codellama-34b-v2.Q4_K_M.gguf";
        String system = "This is a conversation between User and Llama, a friendly chatbot.\n" +
                "Llama is helpful, kind, honest, good at writing, and never fails to answer any " +
                "requests immediately and with precision.\n";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try (LlamaModel model = new LlamaModel(modelPath, modelParams)) {
            System.out.print(system);
            String prompt = system;
            while (true) {
                prompt += "\nUser: ";
                System.out.print("\nUser: ");
                String input = reader.readLine();
                prompt += input;
                System.out.print("Llama: ");
                prompt += "\nLlama: ";
//                String answer = model.complete(prompt, inferParams);
//                System.out.print(answer);
//                prompt += answer;
                boolean responded;
                int counter =0;
                do {
                    responded =false;
                    for (String output:model.generate(prompt, inferParams)) {
                        System.out.print(output);
                        prompt += output;
                        if(!output.trim().isEmpty()){
                            responded =true;
                        }
                    }
                    if(!responded){
                        counter++;
                    }else{
                        counter=0;
                    }
                }while(counter>2);
                System.out.println("next");
            }
        }

    }
}
