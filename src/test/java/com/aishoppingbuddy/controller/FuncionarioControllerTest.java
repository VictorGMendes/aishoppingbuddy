package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Credencial;
import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Token;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuncionarioControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(getClass());

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    FuncionarioRepository funcionarioRepository;
    @Autowired
    ParceiroRepository parceiroRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @Test
    public void givenFuncionario_whenPost_shouldBeRegistered() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // headers.set("Authorization", "Bearer your-token");

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Nome Teste");
        funcionario.setEmail("teste@email.com");
        funcionario.setSenha("senhaTeste");

        String requestBody = objectMapper.writeValueAsString(funcionario);

        log.info(requestBody);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/cadastrar/1";

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);
        log.info(response.getBody());

        var found = funcionarioRepository.findByEmail("teste@email.com");

        assertEquals(201, response.getStatusCode().value());
        assertEquals(objectMapper.writeValueAsString(found),response.getBody());

    }

    @Test
    public void givenCredentials_whenLogin_shouldReturnToken () throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Nome Teste");
        funcionario.setEmail("teste@email.com");
        funcionario.setSenha(encoder.encode("senhaTeste"));
        var parceiro = parceiroRepository.findById(1L)
                .orElseThrow();
        funcionario.setParceiro(parceiro);
        funcionarioRepository.save(funcionario);

        Credencial credentials = new Credencial("teste@email.com","senhaTeste");

        String requestBody = objectMapper.writeValueAsString(credentials);

        log.info(requestBody);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/login";

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);
        var token = tokenService.generateToken(credentials);
        log.info(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(token,objectMapper.readValue(response.getBody(), Token.class));
    }

}
