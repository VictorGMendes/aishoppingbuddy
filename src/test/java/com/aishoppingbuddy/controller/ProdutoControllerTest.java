package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.*;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.repository.ProdutoRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProdutoControllerTest {

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
    ProdutoRepository produtoRepository;

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
    // — Cria alguns produtos de parceiros diferentes
    // — Faz a chamada GET na API
    // — Verifica se o código foi 200
    // — Verifica se o retorno possuí 2 elementos
    public void givenProdutos_whenGet_shouldReturnAllProdutosFromParceiroOfToken() throws Exception {

        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Parceiro pa2 = new Parceiro();
        pa2.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        pa2.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        pa2.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(pa2);

        Produto pr1 = new Produto();
        pr1.setNome(faker.commerce().productName());
        pr1.setCategoria(faker.commerce().material());
        pr1.setTipo(faker.commerce().color());
        double randomValue = .99 + (random.nextDouble() * (100000 - .99));
        pr1.setValor(Math.round(randomValue * Math.pow(10, 2)) / Math.pow(10, 2));
        pr1.setDescricao(faker.lorem().characters(1,100));
        pr1.setParceiro(pa1);
        produtoRepository.save(pr1);

        Produto pr2 = new Produto();
        pr2.setNome(faker.commerce().productName());
        pr2.setCategoria(faker.commerce().material());
        pr2.setTipo(faker.commerce().color());
        double randomValue2 = .99 + (random.nextDouble() * (100000 - .99));
        pr2.setValor(Math.round(randomValue2 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr2.setDescricao(faker.lorem().characters(1,100));
        pr2.setParceiro(pa1);
        produtoRepository.save(pr2);

        Produto pr3 = new Produto();
        pr3.setNome(faker.commerce().productName());
        pr3.setCategoria(faker.commerce().material());
        pr3.setTipo(faker.commerce().color());
        double randomValue3 = .99 + (random.nextDouble() * (100000 - .99));
        pr3.setValor(Math.round(randomValue3 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr3.setDescricao(faker.lorem().characters(1,100));
        pr3.setParceiro(pa2);
        produtoRepository.save(pr3);

        Produto pr4 = new Produto();
        pr4.setNome(faker.commerce().productName());
        pr4.setCategoria(faker.commerce().material());
        pr4.setTipo(faker.commerce().color());
        double randomValue4 = .99 + (random.nextDouble() * (100000 - .99));
        pr4.setValor(Math.round(randomValue4 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr4.setDescricao(faker.lorem().characters(1,100));
        pr4.setParceiro(pa2);
        produtoRepository.save(pr4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/produto";
        ResponseEntity<CustomPageImpl<Produto>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<CustomPageImpl<Produto>>() {});

        PageImpl<Produto> page = response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(page);
        assertEquals(2,page.getContent().size());
    }

    @Test
    // # Testa o GET por ID
    // — Cria o token com createToken()
    // — Cria alguns produtos diferentes
    // — Faz a chamada GET na API com ID:2
    // — Verifica se o código foi 200
    // — Verifica se os dados retorno são os mesmos do produto de ID:2
    public void givenProdutos_whenGetById_shouldReturnProdutoById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Parceiro pa2 = new Parceiro();
        pa2.setNomeFantasia(faker.company().name());
        String cnpj1 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        pa2.setCnpj(cnpj1);
        long minDay1 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay1 = LocalDate.now().toEpochDay();
        long randomDay1 = ThreadLocalRandom.current().nextLong(minDay1, maxDay1);
        pa2.setDataEntrada(LocalDate.ofEpochDay(randomDay1));
        parceiroRepository.save(pa2);

        Produto pr1 = new Produto();
        pr1.setNome(faker.commerce().productName());
        pr1.setCategoria(faker.commerce().material());
        pr1.setTipo(faker.commerce().color());
        double randomValue = .99 + (random.nextDouble() * (100000 - .99));
        pr1.setValor(Math.round(randomValue * Math.pow(10, 2)) / Math.pow(10, 2));
        pr1.setDescricao(faker.lorem().characters(1,100));
        pr1.setParceiro(pa1);
        produtoRepository.save(pr1);

        Produto pr2 = new Produto();
        pr2.setNome(faker.commerce().productName());
        pr2.setCategoria(faker.commerce().material());
        pr2.setTipo(faker.commerce().color());
        double randomValue2 = .99 + (random.nextDouble() * (100000 - .99));
        pr2.setValor(Math.round(randomValue2 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr2.setDescricao(faker.lorem().characters(1,100));
        pr2.setParceiro(pa1);
        produtoRepository.save(pr2);

        Produto pr3 = new Produto();
        pr3.setNome(faker.commerce().productName());
        pr3.setCategoria(faker.commerce().material());
        pr3.setTipo(faker.commerce().color());
        double randomValue3 = .99 + (random.nextDouble() * (100000 - .99));
        pr3.setValor(Math.round(randomValue3 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr3.setDescricao(faker.lorem().characters(1,100));
        pr3.setParceiro(pa2);
        produtoRepository.save(pr3);

        Produto pr4 = new Produto();
        pr4.setNome(faker.commerce().productName());
        pr4.setCategoria(faker.commerce().material());
        pr4.setTipo(faker.commerce().color());
        double randomValue4 = .99 + (random.nextDouble() * (100000 - .99));
        pr4.setValor(Math.round(randomValue4 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr4.setDescricao(faker.lorem().characters(1,100));
        pr4.setParceiro(pa2);
        produtoRepository.save(pr4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/produto/2";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, String.class);

        var found = objectMapper.readValue(response.getBody(), Produto.class);
        log.info(found.toString());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(pr2.getId(),found.getId());
        assertEquals(pr2.getNome(),found.getNome());
        assertEquals(pr2.getTipo(),found.getTipo());
        assertEquals(pr2.getDescricao(),found.getDescricao());
        assertEquals(pr2.getCategoria(),found.getCategoria());
        assertEquals(pr2.getValor(),found.getValor());
    }

    @Test
    // # Testa cadastro de um Produto
    // — Cria o token com createToken()
    // — Cria um produto
    // — Faz a chamada na API para cadastrar
    // — Verifica se o código foi 201
    // — Verifica se o produto cadastrado possuí os dados enviados
    public void givenProduto_whenPost_shouldBeCreated() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = new Produto();
        pr1.setNome(faker.commerce().productName());
        pr1.setCategoria(faker.commerce().material());
        pr1.setTipo(faker.commerce().color());
        double randomValue = .99 + (random.nextDouble() * (100000 - .99));
        pr1.setValor(Math.round(randomValue * Math.pow(10, 2)) / Math.pow(10, 2));
        pr1.setDescricao(faker.lorem().characters(1,100));
        pr1.setParceiro(pa1);
        produtoRepository.save(pr1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(pr1);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/produto";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        var found = produtoRepository.findById(1L);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(objectMapper.writeValueAsString(found),response.getBody());
    }

    @Test
    // # Testa o DELETE por ID
    // — Cria o token com createToken()
    // — Cria alguns produtos diferentes
    // — Faz a chamada DELETE na API com ID:3
    // — Verifica se o código foi 204
    // — Verifica se o produto de ID:3 não existe no banco
    public void givenProdutos_whenDeleteById_shouldBeRemovedById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Parceiro pa2 = new Parceiro();
        pa2.setNomeFantasia(faker.company().name());
        String cnpj2 = fakeValuesService.regexify("\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}");
        pa2.setCnpj(cnpj2);
        long minDay2 = LocalDate.of(2023, 1, 1).toEpochDay();
        long maxDay2 = LocalDate.now().toEpochDay();
        long randomDay2 = ThreadLocalRandom.current().nextLong(minDay2, maxDay2);
        pa2.setDataEntrada(LocalDate.ofEpochDay(randomDay2));
        parceiroRepository.save(pa2);

        Produto pr1 = new Produto();
        pr1.setNome(faker.commerce().productName());
        pr1.setCategoria(faker.commerce().material());
        pr1.setTipo(faker.commerce().color());
        double randomValue = .99 + (random.nextDouble() * (100000 - .99));
        pr1.setValor((randomValue * Math.pow(10, 2)) / Math.pow(10, 2));
        pr1.setDescricao(faker.lorem().characters(1,100));
        pr1.setParceiro(pa1);
        produtoRepository.save(pr1);

        Produto pr2 = new Produto();
        pr2.setNome(faker.commerce().productName());
        pr2.setCategoria(faker.commerce().material());
        pr2.setTipo(faker.commerce().color());
        double randomValue2 = .99 + (random.nextDouble() * (100000 - .99));
        pr2.setValor((randomValue2 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr2.setDescricao(faker.lorem().characters(1,100));
        pr2.setParceiro(pa1);
        produtoRepository.save(pr2);

        Produto pr3 = new Produto();
        pr3.setNome(faker.commerce().productName());
        pr3.setCategoria(faker.commerce().material());
        pr3.setTipo(faker.commerce().color());
        double randomValue3 = .99 + (random.nextDouble() * (100000 - .99));
        pr3.setValor((randomValue3 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr3.setDescricao(faker.lorem().characters(1,100));
        pr3.setParceiro(pa2);
        produtoRepository.save(pr3);

        Produto pr4 = new Produto();
        pr4.setNome(faker.commerce().productName());
        pr4.setCategoria(faker.commerce().material());
        pr4.setTipo(faker.commerce().color());
        double randomValue4 = .99 + (random.nextDouble() * (100000 - .99));
        pr4.setValor((randomValue4 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr4.setDescricao(faker.lorem().characters(1,100));
        pr4.setParceiro(pa2);
        produtoRepository.save(pr4);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/produto/2";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, String.class);

        assertEquals(204, response.getStatusCode().value());
        assertFalse(parceiroRepository.findAll().contains(pr2));

    }

    @Test
    // # Testa o PUT por ID
    // — Cria o token com createToken()
    // — Cria um produto
    // — Cria outro produto com dados diferentes sem salvar no banco
    // — Faz a chamada PUT na API com ID:1 e com corpo do outro produto
    // — Verifica se o código foi 200
    // — Verifica se os dados do produto de ID:1 são os dados atualizados
    public void givenProduto_whenPutId_shouldUpdateById() throws Exception {
        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = new Produto();
        pr1.setNome(faker.commerce().productName());
        pr1.setCategoria(faker.commerce().material());
        pr1.setTipo(faker.commerce().color());
        double randomValue = .99 + (random.nextDouble() * (100000 - .99));
        pr1.setValor((randomValue * Math.pow(10, 2)) / Math.pow(10, 2));
        pr1.setDescricao(faker.lorem().characters(1,100));
        pr1.setParceiro(pa1);
        produtoRepository.save(pr1);

        Produto pr2 = new Produto();
        pr2.setNome(faker.commerce().productName());
        pr2.setCategoria(faker.commerce().material());
        pr2.setTipo(faker.commerce().color());
        double randomValue2 = .99 + (random.nextDouble() * (100000 - .99));
        pr2.setValor((randomValue2 * Math.pow(10, 2)) / Math.pow(10, 2));
        pr2.setDescricao(faker.lorem().characters(1,100));
        pr2.setParceiro(pa1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(pr2);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/produto/1";
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);

        assertEquals(200, response.getStatusCode().value());
        var found = produtoRepository.findById(1L)
                .orElseThrow();
        assertEquals(pr2.getNome(),found.getNome());
        assertEquals(pr2.getTipo(),found.getTipo());
        assertEquals(pr2.getDescricao(),found.getDescricao());
        assertEquals(pr2.getCategoria(),found.getCategoria());
        assertEquals(pr2.getValor(),found.getValor());

    }

}
