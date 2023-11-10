package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Credencial;
import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Token;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParceiroControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(getClass());
    Faker faker = new Faker(new Locale("pt-BR"));

    ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();
    
    @Autowired
    ParceiroRepository parceiroRepository;

    public Token createToken() throws Exception {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Nome Teste");
        funcionario.setEmail("teste@email.com");
        funcionario.setSenha("senhaTeste");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntityCadastro = new HttpEntity<>(objectMapper.writeValueAsString(funcionario), headers);
        ResponseEntity<String> responseCadastro = restTemplate.exchange(
                "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/cadastrar/1",
                HttpMethod.POST,
                requestEntityCadastro,
                String.class);

        Credencial credencial = new Credencial(funcionario.getEmail(),funcionario.getSenha());
        HttpEntity<String> requestEntityLogin = new HttpEntity<>(objectMapper.writeValueAsString(credencial), headers);
        ResponseEntity<String> responseLogin = restTemplate.exchange(
                "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/login",
                HttpMethod.POST,
                requestEntityLogin,
                String.class);

        return objectMapper.readValue(responseLogin.getBody(), Token.class);
    }
    
    @Test
    // # Testa o GET
    // — Cria o token com createToken()
    // — Cria alguns parceiros diferentes
    // — Faz a chamada GET na API
    // — Verifica se o código foi 200
    // — Verifica se o retorno possuí 5 elementos
    public void givenParceiros_whenGet_shouldReturnAllParceiros() throws Exception {

        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        Parceiro p1 = new Parceiro();
        p1.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p1.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        p1.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(p1);

        Parceiro p2 = new Parceiro();
        p2.setNomeFantasia(faker.company().name());
        String cnpj2 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p2.setCnpj(cnpj2);
        long minDay2 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay2 = LocalDate.now().toEpochDay();
        long randomDay2 = ThreadLocalRandom.current().nextLong(minDay2, maxDay2);
        p2.setDataEntrada(LocalDate.ofEpochDay(randomDay2));
        parceiroRepository.save(p2);

        Parceiro p3 = new Parceiro();
        p3.setNomeFantasia(faker.company().name());
        String cnpj3 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        log.info(cnpj3);
        p3.setCnpj(cnpj3);
        long minDay3 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay3 = LocalDate.now().toEpochDay();
        long randomDay3 = ThreadLocalRandom.current().nextLong(minDay3, maxDay3);
        p3.setDataEntrada(LocalDate.ofEpochDay(randomDay3));
        parceiroRepository.save(p3);

        Parceiro p4 = new Parceiro();
        p4.setNomeFantasia(faker.company().name());
        String cnpj4 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        log.info(cnpj4);
        p4.setCnpj(cnpj4);
        long minDay4 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay4 = LocalDate.now().toEpochDay();
        long randomDay4 = ThreadLocalRandom.current().nextLong(minDay4, maxDay4);
        p4.setDataEntrada(LocalDate.ofEpochDay(randomDay4));
        parceiroRepository.save(p4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/parceiro";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, String.class);

        var list = objectMapper.readValue(response.getBody(), List.class);
        log.info(list.toString());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5,list.size());

    }

    @Test
    // # Testa o GET por ID
    // — Cria o token com createToken()
    // — Cria alguns parceiros diferentes
    // — Faz a chamada GET na API com ID:3
    // — Verifica se o código foi 200
    // — Verifica se o retorno é o parceiro de ID:3
    public void givenParceiros_whenGetById_shouldReturnParceiroById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        Parceiro p1 = new Parceiro();
        p1.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p1.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        p1.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(p1);

        Parceiro p2 = new Parceiro();
        p2.setNomeFantasia(faker.company().name());
        String cnpj2 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p2.setCnpj(cnpj2);
        long minDay2 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay2 = LocalDate.now().toEpochDay();
        long randomDay2 = ThreadLocalRandom.current().nextLong(minDay2, maxDay2);
        p2.setDataEntrada(LocalDate.ofEpochDay(randomDay2));
        parceiroRepository.save(p2);

        Parceiro p3 = new Parceiro();
        p3.setNomeFantasia(faker.company().name());
        String cnpj3 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p3.setCnpj(cnpj3);
        long minDay3 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay3 = LocalDate.now().toEpochDay();
        long randomDay3 = ThreadLocalRandom.current().nextLong(minDay3, maxDay3);
        p3.setDataEntrada(LocalDate.ofEpochDay(randomDay3));
        parceiroRepository.save(p3);

        Parceiro p4 = new Parceiro();
        p4.setNomeFantasia(faker.company().name());
        String cnpj4 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p4.setCnpj(cnpj4);
        long minDay4 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay4 = LocalDate.now().toEpochDay();
        long randomDay4 = ThreadLocalRandom.current().nextLong(minDay4, maxDay4);
        p4.setDataEntrada(LocalDate.ofEpochDay(randomDay4));
        parceiroRepository.save(p4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/parceiro/3";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, String.class);

        var found = objectMapper.readValue(response.getBody(), Parceiro.class);
        log.info(found.toString());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(p2,found);
    }

    @Test
    // # Testa cadastro de um Parceiro
    // — Cria o token com createToken()
    // — Cria um parceiro
    // — Faz a chamada na API para cadastrar
    // — Verifica se o código foi 201
    // — Verifica se o parceiro cadastrado possuí os dados enviados
    public void givenParceiro_whenPost_shouldBeCreated() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        Parceiro p1 = new Parceiro();
        p1.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p1.setCnpj(cnpj1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(p1);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/parceiro";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        var found = parceiroRepository.findById(2L);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(objectMapper.writeValueAsString(found),response.getBody());
    }

    @Test
    // # Testa o DELETE por ID
    // — Cria o token com createToken()
    // — Cria alguns parceiros diferentes
    // — Faz a chamada DELETE na API com ID:3
    // — Verifica se o código foi 204
    // — Verifica se o parceiro de ID:3 não existe no banco
    public void givenParceiro_whenDeleteById_shouldBeRemoved() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        Parceiro p1 = new Parceiro();
        p1.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p1.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        p1.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(p1);

        Parceiro p2 = new Parceiro();
        p2.setNomeFantasia(faker.company().name());
        String cnpj2 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p2.setCnpj(cnpj2);
        long minDay2 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay2 = LocalDate.now().toEpochDay();
        long randomDay2 = ThreadLocalRandom.current().nextLong(minDay2, maxDay2);
        p2.setDataEntrada(LocalDate.ofEpochDay(randomDay2));
        parceiroRepository.save(p2);

        Parceiro p3 = new Parceiro();
        p3.setNomeFantasia(faker.company().name());
        String cnpj3 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p3.setCnpj(cnpj3);
        long minDay3 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay3 = LocalDate.now().toEpochDay();
        long randomDay3 = ThreadLocalRandom.current().nextLong(minDay3, maxDay3);
        p3.setDataEntrada(LocalDate.ofEpochDay(randomDay3));
        parceiroRepository.save(p3);

        Parceiro p4 = new Parceiro();
        p4.setNomeFantasia(faker.company().name());
        String cnpj4 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p4.setCnpj(cnpj4);
        long minDay4 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay4 = LocalDate.now().toEpochDay();
        long randomDay4 = ThreadLocalRandom.current().nextLong(minDay4, maxDay4);
        p4.setDataEntrada(LocalDate.ofEpochDay(randomDay4));
        parceiroRepository.save(p4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/parceiro/3";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, String.class);

        assertEquals(204, response.getStatusCode().value());
        assertFalse(parceiroRepository.findAll().contains(p2));

    }

    @Test
    // # Testa o PUT por ID
    // — Cria o token com createToken()
    // — Cria um parceiro
    // — Cria outro parceiro com dados diferentes sem salvar no banco
    // — Faz a chamada PUT na API com ID:2 e com corpo do outro parceiro
    // — Verifica se o código foi 204
    // — Verifica se os dados do pareceiro de ID:2 são os dados atualizados
    public void givenParceiro_whenPutId_shouldUpdateById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        Parceiro p1 = new Parceiro();
        p1.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p1.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        p1.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(p1);

        Parceiro p2 = new Parceiro();
        p2.setNomeFantasia(faker.company().name());
        String cnpj2 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        p2.setCnpj(cnpj2);
        long minDay2 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay2 = LocalDate.now().toEpochDay();
        long randomDay2 = ThreadLocalRandom.current().nextLong(minDay2, maxDay2);
        p2.setDataEntrada(LocalDate.ofEpochDay(randomDay2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(p2);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/parceiro/2";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);

        assertEquals(200, response.getStatusCode().value());
        var found = parceiroRepository.findById(2L)
                .orElseThrow();
        assertEquals(p2.getNomeFantasia(),found.getNomeFantasia());

    }

}
