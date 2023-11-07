package com.aishoppingbuddy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.io.IOException;

@SpringBootTest
public class ChatGPTTest {

    @Value("${apiKey}")
    private String apiKey;

    @Test
    public void givenPrompt_whenSend_shouldReturnResult() throws IOException, InterruptedException {

        String endpoint = "https://api.openai.com/v1/engines/davinci/completions";

        String prompt = "Bom dia!";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"" + prompt + "\",\"max_tokens\":50}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            System.out.println("Response from ChatGPT: " + responseBody);
        } else {
            System.err.println("API request failed with status code: " + response.statusCode());
            System.err.println("Error message: " + response.body());
        }
    }

}
