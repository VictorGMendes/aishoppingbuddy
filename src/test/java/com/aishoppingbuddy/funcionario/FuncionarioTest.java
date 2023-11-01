package com.aishoppingbuddy.funcionario;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuncionarioTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void givenFuncionario_whenPost_thenShouldBeRegistered() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // headers.set("Authorization", "Bearer your-token");

        String requestBody = "{\"nome\":\"Nome Teste\",\"email\":\"teste@email.com\",\"senha\":\"senhateste\"}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/cadastrar/1";

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);
        log.info(response.getBody());
        assertEquals(201, response.getStatusCode().value()); // Replace with the expected status code
        assertNotNull(response.getBody());

    }

}
