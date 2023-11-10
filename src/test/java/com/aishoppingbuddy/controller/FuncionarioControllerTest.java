package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Credencial;
import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Token;
import com.aishoppingbuddy.repository.FuncionarioRepository;
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
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FuncionarioControllerTest {

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
    FuncionarioRepository funcionarioRepository;
    @Autowired
    ParceiroRepository parceiroRepository;

    @Autowired
    PasswordEncoder encoder;

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
    // # Testa cadastro de um funcionário
    // — Cria um funcionário
    // — Faz a chamada na API para cadastrar
    // — Verifica se o código foi 201
    // — Verifica se o funcionário cadastrado possuí os dados enviados
    public void givenFuncionario_whenPost_shouldBeRegistered() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

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
    // # Testa o login de funcionário
    // — Cria um funcionário cadastra
    // — Faz a chamada POST na API para login
    // — Verifica se o código foi 201
    // — Verifica se foi retornado um token
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

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(objectMapper.readValue(response.getBody(), Token.class));
    }

    @Test
    // # Testa o método createToken()
    // — Cria um funcionário
    // — Efetua o login desse funcionário
    // — Retorna o token do login
    // — Verifica se token foi retornado
    // — Verifica se o funcionário foi criado
    public void withCreateToken_shouldCreateFuncionarioAndToken() throws Exception {

        var token = createToken();
        var funcionario = funcionarioRepository.findById(1L);

        log.info(token.toString());
        log.info(funcionario.toString());

        assertNotNull(token);
        assertNotNull(funcionario);

    }

    @Test
    // # Testa o GET
    // — Cria o token com createToken()
    // — Cria alguns funcionários de parceiros diferentes
    // — Faz a chamada GET na API
    // — Verifica se o código foi 200
    // — Verifica se o retorno possuí 5 elementos
    public void givenFuncionarios_whenGet_shouldReturnAll() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        Parceiro p1 = new Parceiro();
        p1.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        log.info(cnpj1);
        p1.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        p1.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(p1);

        Parceiro p2 = new Parceiro();
        p2.setNomeFantasia(faker.company().name());
        String cnpj2 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        log.info(cnpj2);
        p2.setCnpj(cnpj2);
        long minDay2 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay2 = LocalDate.now().toEpochDay();
        long randomDay2 = ThreadLocalRandom.current().nextLong(minDay2, maxDay2);
        p2.setDataEntrada(LocalDate.ofEpochDay(randomDay2));
        parceiroRepository.save(p2);

        Funcionario f1 = new Funcionario();
        f1.setNome(faker.name().fullName());
        f1.setEmail(faker.internet().emailAddress());
        f1.setSenha(encoder.encode(faker.internet().password()));
        f1.setParceiro(p1);
        funcionarioRepository.save(f1);

        Funcionario f2 = new Funcionario();
        f2.setNome(faker.name().fullName());
        f2.setEmail(faker.internet().emailAddress());
        f2.setSenha(encoder.encode(faker.internet().password()));
        f2.setParceiro(p1);
        funcionarioRepository.save(f2);

        Funcionario f3 = new Funcionario();
        f3.setNome(faker.name().fullName());
        f3.setEmail(faker.internet().emailAddress());
        f3.setSenha(encoder.encode(faker.internet().password()));
        f3.setParceiro(p2);
        funcionarioRepository.save(f3);

        Funcionario f4 = new Funcionario();
        f4.setNome(faker.name().fullName());
        f4.setEmail(faker.internet().emailAddress());
        f4.setSenha(encoder.encode(faker.internet().password()));
        f4.setParceiro(p2);
        funcionarioRepository.save(f4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, String.class);

        var list = objectMapper.readValue(response.getBody(),List.class);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(5,list.size());

    }

    @Test
    // # Testa o GET por ID
    // — Cria o token com createToken()
    // — Cria alguns funcionários de parceiros diferentes
    // — Faz a chamada GET na API com ID:3
    // — Verifica se o código foi 200
    // — Verifica se o retorno é o funcionário de ID:3
    public void givenFuncionarios_whenGetById_shouldReturnById() throws Exception {
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

        Funcionario f1 = new Funcionario();
        f1.setNome(faker.name().fullName());
        f1.setEmail(faker.internet().emailAddress());
        f1.setSenha(encoder.encode(faker.internet().password()));
        f1.setParceiro(p1);
        funcionarioRepository.save(f1);

        Funcionario f2 = new Funcionario();
        f2.setNome(faker.name().fullName());
        f2.setEmail(faker.internet().emailAddress());
        f2.setSenha(encoder.encode(faker.internet().password()));
        f2.setParceiro(p1);
        funcionarioRepository.save(f2);

        Funcionario f3 = new Funcionario();
        f3.setNome(faker.name().fullName());
        f3.setEmail(faker.internet().emailAddress());
        f3.setSenha(encoder.encode(faker.internet().password()));
        f3.setParceiro(p2);
        funcionarioRepository.save(f3);

        Funcionario f4 = new Funcionario();
        f4.setNome(faker.name().fullName());
        f4.setEmail(faker.internet().emailAddress());
        f4.setSenha(encoder.encode(faker.internet().password()));
        f4.setParceiro(p2);
        funcionarioRepository.save(f4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/3";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, String.class);

        var found = objectMapper.readValue(response.getBody(),Funcionario.class);
        log.info(found.toString());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(f2,found);

    }

    @Test
    // # Testa o DELETE por ID
    // — Cria o token com createToken()
    // — Cria alguns funcionários de parceiros diferentes
    // — Faz a chamada DELETE na API com ID:3
    // — Verifica se o código foi 204
    // — Verifica se o funcionário de ID:3 não existe no banco
    public void givenFuncionarioId_whenDeleteById_shouldBeDeletedById() throws Exception {
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

        Funcionario f1 = new Funcionario();
        f1.setNome(faker.name().fullName());
        f1.setEmail(faker.internet().emailAddress());
        f1.setSenha(encoder.encode(faker.internet().password()));
        f1.setParceiro(p1);
        funcionarioRepository.save(f1);

        Funcionario f2 = new Funcionario();
        f2.setNome(faker.name().fullName());
        f2.setEmail(faker.internet().emailAddress());
        f2.setSenha(encoder.encode(faker.internet().password()));
        f2.setParceiro(p1);
        funcionarioRepository.save(f2);

        Funcionario f3 = new Funcionario();
        f3.setNome(faker.name().fullName());
        f3.setEmail(faker.internet().emailAddress());
        f3.setSenha(encoder.encode(faker.internet().password()));
        f3.setParceiro(p2);
        funcionarioRepository.save(f3);

        Funcionario f4 = new Funcionario();
        f4.setNome(faker.name().fullName());
        f4.setEmail(faker.internet().emailAddress());
        f4.setSenha(encoder.encode(faker.internet().password()));
        f4.setParceiro(p2);
        funcionarioRepository.save(f4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/3";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, String.class);

        assertEquals(204, response.getStatusCode().value());
        assertFalse(funcionarioRepository.findAll().contains(f1));
        
    }

    @Test
    // # Testa o PUT por ID
    // — Cria o token com createToken()
    // — Cria um funcionário
    // — Cria outro funcionário com dados diferentes sem salvar no banco
    // — Faz a chamada PUT na API com ID:2 e com corpo do outro funcionário
    // — Verifica se o código foi 200
    // — Verifica se os dados do funcionário de ID:2 são os dados atualizados
    public void givenFuncionario_whenPutFuncionarioById_shouldUpdateById() throws Exception {
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

        Funcionario f1 = new Funcionario();
        f1.setNome(faker.name().fullName());
        f1.setEmail(faker.internet().emailAddress());
        f1.setSenha(encoder.encode(faker.internet().password()));
        f1.setParceiro(p1);
        funcionarioRepository.save(f1);

        Funcionario f2 = new Funcionario();
        f2.setNome(faker.name().fullName());
        f2.setEmail(faker.internet().emailAddress());
        f2.setSenha(faker.internet().password());
        f2.setParceiro(p1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer "+token.token());
        String requestBody = objectMapper.writeValueAsString(f2);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/funcionario/2";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);

        assertEquals(200, response.getStatusCode().value());
        var found = funcionarioRepository.findById(2L)
                .orElseThrow();
        assertEquals(f2.getNome(),found.getNome());
        assertEquals(f2.getEmail(),found.getEmail());
        assertEquals(f2.getParceiro().getId(),found.getParceiro().getId());

    }
    
}
