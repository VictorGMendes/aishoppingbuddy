package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.*;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.repository.ProdutoRepository;
import com.aishoppingbuddy.repository.RecomendacaoRepository;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecomendacaoControllerTest {

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

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RecomendacaoRepository recomendacaoRepository;

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
    public void givenProdutosAndUsuario_whenPOST_shouldCreateRecomendacao() throws Exception {

        var token = createToken();
        log.info(token.token());

        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("pt-BR"), new RandomService());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(pa1).build();
        Produto pr2 = Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(pa1).build();
        produtoRepository.saveAll(List.of(pr1,pr2));

        Usuario u1 = Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build();
        usuarioRepository.save(u1);

        Recomendacao r1 = new Recomendacao();
        r1.setProdutoList(List.of(pr1, pr2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        String requestBody = objectMapper.writeValueAsString(r1);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        String baseUrl = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u1.getId();
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        var found = recomendacaoRepository.findById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(found);

    }

}
