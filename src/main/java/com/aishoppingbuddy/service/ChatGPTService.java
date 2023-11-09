package com.aishoppingbuddy.service;

import com.aishoppingbuddy.model.ChatGPTResponse;
import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class ChatGPTService {

    @Value("${apiKey}")
    private String apiKey;

    Logger log = LoggerFactory.getLogger(getClass());

    ObjectMapper objectMapper = new ObjectMapper();

    public String generateMessage(List<Produto> produtoList, Usuario usuario) throws IOException, InterruptedException {

        String endpoint = "https://api.openai.com/v1/chat/completions";

        String prompt = "Crie uma recomendação com menos de 500 caracteres, ";

        for (int i = 0; i < produtoList.size(); i++) {
            prompt = prompt +
                    "do "+produtoList.get(i).getTipo()+" "+produtoList.get(i).getNome()+", "+produtoList.get(i).getDescricao()+", ";
        }

        prompt = prompt + " para "+usuario.getNome()+", o texto deve ser escrito de forma humana e animada, mostre autenticidade.";

        log.info(prompt);
        String body = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"Você é um especialista em recomendar produtos através de textos curtos com menos de 500 caracteres. Seus textos são felizes, simpáticos e entusiasmados\"},{\"role\":\"user\",\"content\":\"" + prompt + "\"}]}";
        log.info(body);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        log.info(request.toString());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            try {
                ChatGPTResponse chatGPTResponse = objectMapper.readValue(responseBody, ChatGPTResponse.class);
                log.info("Response from ChatGPT: " + chatGPTResponse.toString());
                return chatGPTResponse.getChoices().get(0).getMessage().getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("API request failed with status code: " + response.statusCode());
            log.info("Error message: " + response.body());
        }
        return null;
    }

}
