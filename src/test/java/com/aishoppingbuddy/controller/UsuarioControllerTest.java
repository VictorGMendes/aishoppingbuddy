package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.*;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.repository.UsuarioRepository;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuarioControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(getClass());
    Faker faker = new Faker(new Locale("pt-BR"));
    Random random = new Random();

    ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();

    @Autowired
    ParceiroRepository parceiroRepository;
    
    @Autowired
    UsuarioRepository usuarioRepository;

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
    // — Cria alguns usuários diferentes
    // — Faz a chamada GET na API
    // — Verifica se o código foi 200
    // — Verifica se o retorno possuí 4 elementos
    public void givenUsuarios_whenGet_shouldReturnAllUsuariosFromParceiroOfToken() throws Exception {

        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        String[] genero = {"F", "M", "NB"};

        Usuario u1 = new Usuario();
        u1.setNome(faker.name().fullName());
        u1.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u1.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u1.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday1 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday1 = LocalDate.now().toEpochDay();
        long randomBirthday1 = ThreadLocalRandom.current().nextLong(minBirthday1, maxBirthday1);
        u1.setDataNascimento(LocalDate.ofEpochDay(randomBirthday1));
        usuarioRepository.save(u1);

        Usuario u2 = new Usuario();
        u2.setNome(faker.name().fullName());
        u2.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u2.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u2.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday2 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday2 = LocalDate.now().toEpochDay();
        long randomBirthday2 = ThreadLocalRandom.current().nextLong(minBirthday2, maxBirthday2);
        u2.setDataNascimento(LocalDate.ofEpochDay(randomBirthday2));
        usuarioRepository.save(u2);

        Usuario u3 = new Usuario();
        u3.setNome(faker.name().fullName());
        u3.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{3}$/"));
        u3.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u3.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday3 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday3 = LocalDate.now().toEpochDay();
        long randomBirthday3 = ThreadLocalRandom.current().nextLong(minBirthday3, maxBirthday3);
        u3.setDataNascimento(LocalDate.ofEpochDay(randomBirthday3));
        usuarioRepository.save(u3);

        Usuario u4 = new Usuario();
        u4.setNome(faker.name().fullName());
        u4.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{4}$/"));
        u4.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u4.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday4 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday4 = LocalDate.now().toEpochDay();
        long randomBirthday4 = ThreadLocalRandom.current().nextLong(minBirthday4, maxBirthday4);
        u4.setDataNascimento(LocalDate.ofEpochDay(randomBirthday4));
        usuarioRepository.save(u4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/usuario";
        ResponseEntity<CustomPageImpl<Usuario>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<CustomPageImpl<Usuario>>() {});

        PageImpl<Usuario> page = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(page);
        assertEquals(4,page.getContent().size());
    }

    @Test
    // # Testa o GET por ID
    // — Cria o token com createToken()
    // — Cria alguns produtos diferentes
    // — Faz a chamada GET na API com ID:2
    // — Verifica se o código foi 200
    // — Verifica se os dados retorno são os mesmos do usuário de ID:2
    public void givenUsuarios_whenGetById_shouldReturnUsuarioById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        String[] genero = {"F", "M", "NB"};

        Usuario u1 = new Usuario();
        u1.setNome(faker.name().fullName());
        u1.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u1.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u1.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday1 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday1 = LocalDate.now().toEpochDay();
        long randomBirthday1 = ThreadLocalRandom.current().nextLong(minBirthday1, maxBirthday1);
        u1.setDataNascimento(LocalDate.ofEpochDay(randomBirthday1));
        usuarioRepository.save(u1);

        Usuario u2 = new Usuario();
        u2.setNome(faker.name().fullName());
        u2.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u2.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u2.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday2 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday2 = LocalDate.now().toEpochDay();
        long randomBirthday2 = ThreadLocalRandom.current().nextLong(minBirthday2, maxBirthday2);
        u2.setDataNascimento(LocalDate.ofEpochDay(randomBirthday2));
        usuarioRepository.save(u2);

        Usuario u3 = new Usuario();
        u3.setNome(faker.name().fullName());
        u3.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{3}$/"));
        u3.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u3.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday3 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday3 = LocalDate.now().toEpochDay();
        long randomBirthday3 = ThreadLocalRandom.current().nextLong(minBirthday3, maxBirthday3);
        u3.setDataNascimento(LocalDate.ofEpochDay(randomBirthday3));
        usuarioRepository.save(u3);

        Usuario u4 = new Usuario();
        u4.setNome(faker.name().fullName());
        u4.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{4}$/"));
        u4.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u4.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday4 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday4 = LocalDate.now().toEpochDay();
        long randomBirthday4 = ThreadLocalRandom.current().nextLong(minBirthday4, maxBirthday4);
        u4.setDataNascimento(LocalDate.ofEpochDay(randomBirthday4));
        usuarioRepository.save(u4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/usuario/2";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, String.class);

        var found = objectMapper.readValue(response.getBody(), Usuario.class);
        log.info(found.toString());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(u2,found);
    }

    @Test
    // # Testa cadastro de um Usuário
    // — Cria o token com createToken()
    // — Cria um produto
    // — Faz a chamada na API para cadastrar
    // — Verifica se o código foi 201
    // — Verifica se o usuário cadastrado possuí os dados enviados
    public void givenUsuario_whenPost_shouldBeCreated() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        String[] genero = {"F", "M", "NB"};

        Usuario u1 = new Usuario();
        u1.setNome(faker.name().fullName());
        u1.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u1.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u1.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday1 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday1 = LocalDate.now().toEpochDay();
        long randomBirthday1 = ThreadLocalRandom.current().nextLong(minBirthday1, maxBirthday1);
        u1.setDataNascimento(LocalDate.ofEpochDay(randomBirthday1));
        usuarioRepository.save(u1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(u1);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/usuario";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        var found = usuarioRepository.findById(1L);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(objectMapper.writeValueAsString(found),response.getBody());
    }

    @Test
    // # Testa o DELETE por ID
    // — Cria o token com createToken()
    // — Cria alguns produtos diferentes
    // — Faz a chamada DELETE na API com ID:3
    // — Verifica se o código foi 204
    // — Verifica se o produto de ID:2 não existe no banco
    public void givenUsuarios_whenDeleteById_shouldBeRemovedById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        String[] genero = {"F", "M", "NB"};

        Usuario u1 = new Usuario();
        u1.setNome(faker.name().fullName());
        u1.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u1.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u1.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday1 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday1 = LocalDate.now().toEpochDay();
        long randomBirthday1 = ThreadLocalRandom.current().nextLong(minBirthday1, maxBirthday1);
        u1.setDataNascimento(LocalDate.ofEpochDay(randomBirthday1));
        usuarioRepository.save(u1);

        Usuario u2 = new Usuario();
        u2.setNome(faker.name().fullName());
        u2.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u2.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u2.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday2 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday2 = LocalDate.now().toEpochDay();
        long randomBirthday2 = ThreadLocalRandom.current().nextLong(minBirthday2, maxBirthday2);
        u2.setDataNascimento(LocalDate.ofEpochDay(randomBirthday2));
        usuarioRepository.save(u2);

        Usuario u3 = new Usuario();
        u3.setNome(faker.name().fullName());
        u3.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{3}$/"));
        u3.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u3.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday3 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday3 = LocalDate.now().toEpochDay();
        long randomBirthday3 = ThreadLocalRandom.current().nextLong(minBirthday3, maxBirthday3);
        u3.setDataNascimento(LocalDate.ofEpochDay(randomBirthday3));
        usuarioRepository.save(u3);

        Usuario u4 = new Usuario();
        u4.setNome(faker.name().fullName());
        u4.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{4}$/"));
        u4.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u4.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday4 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday4 = LocalDate.now().toEpochDay();
        long randomBirthday4 = ThreadLocalRandom.current().nextLong(minBirthday4, maxBirthday4);
        u4.setDataNascimento(LocalDate.ofEpochDay(randomBirthday4));
        usuarioRepository.save(u4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/usuario/2";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, String.class);

        assertEquals(204, response.getStatusCode().value());
        assertFalse(parceiroRepository.findAll().contains(u2));

    }

    @Test
    // # Testa o PUT por ID
    // — Cria o token com createToken()
    // — Cria um usuario
    // — Cria outro usuario com dados diferentes sem salvar no banco
    // — Faz a chamada PUT na API com ID:1 e com corpo do outro usuario
    // — Verifica se o código foi 200
    // — Verifica se os dados do usuario de ID:1 são os dados atualizados
    public void givenUsuario_whenPutId_shouldUpdateById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        String[] genero = {"F", "M", "NB"};

        Usuario u1 = new Usuario();
        u1.setNome(faker.name().fullName());
        u1.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u1.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u1.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday1 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday1 = LocalDate.now().toEpochDay();
        long randomBirthday1 = ThreadLocalRandom.current().nextLong(minBirthday1, maxBirthday1);
        u1.setDataNascimento(LocalDate.ofEpochDay(randomBirthday1));
        usuarioRepository.save(u1);

        Usuario u2 = new Usuario();
        u2.setNome(faker.name().fullName());
        u2.setCpf(fakeValuesService.regexify("/^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$/"));
        u2.setCep(fakeValuesService.regexify("\\d{5}-\\d{3}"));
        u2.setGenero(genero[new Random().nextInt(genero.length)]);
        long minBirthday2 = LocalDate.of(1900, 1, 1).toEpochDay();
        long maxBirthday2 = LocalDate.now().toEpochDay();
        long randomBirthday2 = ThreadLocalRandom.current().nextLong(minBirthday2, maxBirthday2);
        u2.setDataNascimento(LocalDate.ofEpochDay(randomBirthday2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(u2);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/usuario/1";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);

        assertEquals(200, response.getStatusCode().value());
        var found = usuarioRepository.findById(1L)
                .orElseThrow();
        assertEquals(u2.getNome(),found.getNome());
        assertEquals(u2.getCep(),found.getCep());
        assertEquals(u2.getCpf(),found.getCpf());
        assertEquals(u2.getGenero(),found.getGenero());
        assertEquals(u2.getDataNascimento(),found.getDataNascimento());

    }

}
