package com.sk.netdisk.util.openai;


import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import io.swagger.v3.oas.models.OpenAPI;
import sun.net.www.http.HttpClient;

import java.time.Duration;

/**
 * @author lsj
 */
public class OpenAiClienta {

    private static final String API_KEY = "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";

    public static void main(String[] args) {

        OpenAiService service = new OpenAiService(API_KEY,Duration.ofMinutes(1));
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("1+1等于几")
                .model("babbage")
                .echo(true)
                .build();
        CompletionResult completion = service.createCompletion(completionRequest);
        completion.getChoices().forEach(System.out::println);
    }


}
