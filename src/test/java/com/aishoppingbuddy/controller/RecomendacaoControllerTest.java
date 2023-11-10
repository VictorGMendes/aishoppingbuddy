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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
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
    // # Testa a criação de uma recomendação
    // — Cria o token com createToken()
    // — Cria alguns produtos
    // — Cria alguns usuários
    // — Cria a recomendação com apenas uma lista de produtos
    // — Faz a chamada na API do ChatGPT para criar a mensagem
    // — Verifica se o código foi 200
    // — Verifica se a recomendação foi criada
    public void givenProdutosAndUsuario_whenPOST_shouldCreateRecomendacao() throws Exception {

        var token = createToken();
        log.info(token.token());

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

    @Test
    // # Testa o GET
    // — Cria o token com createToken()
    // — Cria alguns produtos
    // — Cria alguns usuários
    // — Cria algumas recomendações com apenas uma lista de produtos
    // — Faz a chamada na API do ChatGPT para criar as mensagens pra cada recomendação
    // — Faz a chamada na API para retornar todas recomendações da pesquisa
    // — Verifica se os códigos foram 200
    // — Verifica se as recomendações foram criadas
    // — Verifica se o retorno possuí 4 elementos
    public void givenProdutosAndUsuario_whenGET_shouldReturnAll() throws Exception {

        var token = createToken();
        log.info(token.token());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(pa1).build();
        Produto pr2 = Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(pa1).build();
        Produto pr3 = Produto.builder().nome("Bicicleta de Montanha").tipo("Esporte e Lazer").categoria("Aventura").valor(499.99).descricao("Uma bicicleta resistente projetada para trilhas off-road, com suspensão dianteira e pneus robustos para aventuras na natureza.").parceiro(pa1).recomendacaoList(List.of()).build();
        Produto pr4 = Produto.builder().nome("Máquina de Café Expresso").tipo("Eletrodoméstico").categoria("Culinária").valor(199.99).descricao("Uma máquina de café automática que prepara café expresso delicioso com o toque de um botão, perfeita para os amantes de café.").parceiro(pa1).recomendacaoList(List.of()).build();
        produtoRepository.saveAll(List.of(pr1,pr2,pr3,pr4));

        Usuario u1 = Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build();
        Usuario u2 = Usuario.builder().nome("Mateus Iago Kaique Moreira").cep("64000390").cpf("79528133312").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u3 = Usuario.builder().nome("Pietro Ian Barbosa").cep("66913260").cpf("35789752900").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u4 = Usuario.builder().nome("Sara Julia Nair Barbosa").cep("65082585").cpf("38665570519").dataNascimento(LocalDate.now()).genero("F").build();
        usuarioRepository.saveAll(List.of(u1,u2,u3,u4));

        Recomendacao r1 = new Recomendacao();
        r1.setProdutoList(List.of(pr1, pr2));
        Recomendacao r2 = new Recomendacao();
        r2.setProdutoList(List.of(pr1, pr3));
        Recomendacao r3 = new Recomendacao();
        r3.setProdutoList(List.of(pr3, pr2));
        Recomendacao r4 = new Recomendacao();
        r4.setProdutoList(List.of(pr4, pr2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");
        
        String baseUrl1 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u1.getId();
        String requestBody1 = objectMapper.writeValueAsString(r1);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(requestBody1, headers);
        ResponseEntity<String> response1 = restTemplate.exchange(baseUrl1, HttpMethod.POST, requestEntity1, String.class);

        String baseUrl2 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u2.getId();
        String requestBody2 = objectMapper.writeValueAsString(r2);
        HttpEntity<String> requestEntity2 = new HttpEntity<>(requestBody2, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(baseUrl2, HttpMethod.POST, requestEntity2, String.class);

        String baseUrl3 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u3.getId();
        String requestBody3 = objectMapper.writeValueAsString(r3);
        HttpEntity<String> requestEntity3 = new HttpEntity<>(requestBody3, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(baseUrl3, HttpMethod.POST, requestEntity3, String.class);

        String baseUrl4 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u4.getId();
        String requestBody4 = objectMapper.writeValueAsString(r4);
        HttpEntity<String> requestEntity4 = new HttpEntity<>(requestBody4, headers);
        ResponseEntity<String> response4 = restTemplate.exchange(baseUrl4, HttpMethod.POST, requestEntity4, String.class);

        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntityGet = new HttpEntity<>(headersGet);
        String baseUrlGet = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao";
        ResponseEntity<CustomPageImpl<Recomendacao>> responseGet = restTemplate.exchange(baseUrlGet, HttpMethod.GET, requestEntityGet, new ParameterizedTypeReference<CustomPageImpl<Recomendacao>>() {});

        PageImpl<Recomendacao> page = responseGet.getBody();

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(200, response2.getStatusCode().value());
        assertEquals(200, response3.getStatusCode().value());
        assertEquals(200, response4.getStatusCode().value());
        assertEquals(200, responseGet.getStatusCode().value());
        assertEquals(4,page.getContent().size());

    }

    @Test
    // # Testa o GET por ID
    // — Cria o token com createToken()
    // — Cria alguns produtos
    // — Cria alguns usuários
    // — Cria algumas recomendações com apenas uma lista de produtos
    // — Faz a chamada na API do ChatGPT para criar as mensagens pra cada recomendação
    // — Faz a chamada na API para retornar a recomendação por ID
    // — Verifica se os códigos foram 200
    // — Verifica se as recomendações foram criadas
    // — Verifica se o retorno não foi nulo
    public void givenProdutosAndUsuario_whenGETById_shouldReturnById() throws Exception {

        var token = createToken();
        log.info(token.token());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(pa1).build();
        Produto pr2 = Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(pa1).build();
        Produto pr3 = Produto.builder().nome("Bicicleta de Montanha").tipo("Esporte e Lazer").categoria("Aventura").valor(499.99).descricao("Uma bicicleta resistente projetada para trilhas off-road, com suspensão dianteira e pneus robustos para aventuras na natureza.").parceiro(pa1).recomendacaoList(List.of()).build();
        Produto pr4 = Produto.builder().nome("Máquina de Café Expresso").tipo("Eletrodoméstico").categoria("Culinária").valor(199.99).descricao("Uma máquina de café automática que prepara café expresso delicioso com o toque de um botão, perfeita para os amantes de café.").parceiro(pa1).recomendacaoList(List.of()).build();
        produtoRepository.saveAll(List.of(pr1,pr2,pr3,pr4));

        Usuario u1 = Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build();
        Usuario u2 = Usuario.builder().nome("Mateus Iago Kaique Moreira").cep("64000390").cpf("79528133312").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u3 = Usuario.builder().nome("Pietro Ian Barbosa").cep("66913260").cpf("35789752900").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u4 = Usuario.builder().nome("Sara Julia Nair Barbosa").cep("65082585").cpf("38665570519").dataNascimento(LocalDate.now()).genero("F").build();
        usuarioRepository.saveAll(List.of(u1,u2,u3,u4));

        Recomendacao r1 = new Recomendacao();
        r1.setProdutoList(List.of(pr1, pr2));
        Recomendacao r2 = new Recomendacao();
        r2.setProdutoList(List.of(pr1, pr3));
        Recomendacao r3 = new Recomendacao();
        r3.setProdutoList(List.of(pr3, pr2));
        Recomendacao r4 = new Recomendacao();
        r4.setProdutoList(List.of(pr4, pr2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");

        String baseUrl1 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u1.getId();
        String requestBody1 = objectMapper.writeValueAsString(r1);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(requestBody1, headers);
        ResponseEntity<String> response1 = restTemplate.exchange(baseUrl1, HttpMethod.POST, requestEntity1, String.class);

        String baseUrl2 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u2.getId();
        String requestBody2 = objectMapper.writeValueAsString(r2);
        HttpEntity<String> requestEntity2 = new HttpEntity<>(requestBody2, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(baseUrl2, HttpMethod.POST, requestEntity2, String.class);

        String baseUrl3 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u3.getId();
        String requestBody3 = objectMapper.writeValueAsString(r3);
        HttpEntity<String> requestEntity3 = new HttpEntity<>(requestBody3, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(baseUrl3, HttpMethod.POST, requestEntity3, String.class);

        String baseUrl4 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u4.getId();
        String requestBody4 = objectMapper.writeValueAsString(r4);
        HttpEntity<String> requestEntity4 = new HttpEntity<>(requestBody4, headers);
        ResponseEntity<String> response4 = restTemplate.exchange(baseUrl4, HttpMethod.POST, requestEntity4, String.class);

        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntityGet = new HttpEntity<>(headersGet);
        String baseUrlGet = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/2";
        ResponseEntity<String> responseGet = restTemplate.exchange(baseUrlGet, HttpMethod.GET, requestEntityGet, String.class);

        log.info(responseGet.getBody());

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(200, response2.getStatusCode().value());
        assertEquals(200, response3.getStatusCode().value());
        assertEquals(200, response4.getStatusCode().value());
        assertEquals(200, responseGet.getStatusCode().value());
        assertNotNull(responseGet.getBody());

    }

    @Test
    // # Testa o GET por pesquisa
    // — Cria o token com createToken()
    // — Cria alguns produtos
    // — Cria alguns usuários
    // — Cria algumas recomendações com apenas uma lista de produtos
    // — Faz a chamada na API do ChatGPT para criar as mensagens pra cada recomendação
    // — Faz a chamada na API para retornar todas recomendações pela pesquisa
    // — Verifica se os códigos foram 200
    // — Verifica se as recomendações foram criadas
    // — Verifica se o retorno possuí 2 elementos
    public void givenProdutosAndUsuario_whenGETWithSearch_shouldReturnFromSearch() throws Exception {

        var token = createToken();
        log.info(token.token());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(pa1).build();
        Produto pr2 = Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(pa1).build();
        Produto pr3 = Produto.builder().nome("Bicicleta de Montanha").tipo("Esporte e Lazer").categoria("Aventura").valor(499.99).descricao("Uma bicicleta resistente projetada para trilhas off-road, com suspensão dianteira e pneus robustos para aventuras na natureza.").parceiro(pa1).recomendacaoList(List.of()).build();
        Produto pr4 = Produto.builder().nome("Máquina de Café Expresso").tipo("Eletrodoméstico").categoria("Culinária").valor(199.99).descricao("Uma máquina de café automática que prepara café expresso delicioso com o toque de um botão, perfeita para os amantes de café.").parceiro(pa1).recomendacaoList(List.of()).build();
        produtoRepository.saveAll(List.of(pr1,pr2,pr3,pr4));

        Usuario u1 = Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build();
        Usuario u2 = Usuario.builder().nome("Mateus Iago Kaique Moreira").cep("64000390").cpf("79528133312").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u3 = Usuario.builder().nome("Pietro Ian Barbosa").cep("66913260").cpf("35789752900").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u4 = Usuario.builder().nome("Sara Julia Nair Barbosa").cep("65082585").cpf("38665570519").dataNascimento(LocalDate.now()).genero("F").build();
        usuarioRepository.saveAll(List.of(u1,u2,u3,u4));

        Recomendacao r1 = new Recomendacao();
        r1.setProdutoList(List.of(pr1, pr2));
        Recomendacao r2 = new Recomendacao();
        r2.setProdutoList(List.of(pr1, pr3));
        Recomendacao r3 = new Recomendacao();
        r3.setProdutoList(List.of(pr3, pr2));
        Recomendacao r4 = new Recomendacao();
        r4.setProdutoList(List.of(pr4, pr2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");

        String baseUrl1 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u1.getId();
        String requestBody1 = objectMapper.writeValueAsString(r1);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(requestBody1, headers);
        ResponseEntity<String> response1 = restTemplate.exchange(baseUrl1, HttpMethod.POST, requestEntity1, String.class);

        String baseUrl2 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u2.getId();
        String requestBody2 = objectMapper.writeValueAsString(r2);
        HttpEntity<String> requestEntity2 = new HttpEntity<>(requestBody2, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(baseUrl2, HttpMethod.POST, requestEntity2, String.class);

        String baseUrl3 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u3.getId();
        String requestBody3 = objectMapper.writeValueAsString(r3);
        HttpEntity<String> requestEntity3 = new HttpEntity<>(requestBody3, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(baseUrl3, HttpMethod.POST, requestEntity3, String.class);

        String baseUrl4 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u4.getId();
        String requestBody4 = objectMapper.writeValueAsString(r4);
        HttpEntity<String> requestEntity4 = new HttpEntity<>(requestBody4, headers);
        ResponseEntity<String> response4 = restTemplate.exchange(baseUrl4, HttpMethod.POST, requestEntity4, String.class);

        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntityGet = new HttpEntity<>(headersGet);
        String baseUrlGet = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/busca/Smartphone";
        ResponseEntity<CustomPageImpl<Recomendacao>> responseGet = restTemplate.exchange(baseUrlGet, HttpMethod.GET, requestEntityGet, new ParameterizedTypeReference<CustomPageImpl<Recomendacao>>() {});

        PageImpl<Recomendacao> page = responseGet.getBody();
        log.info(page.getContent().toString());

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(200, response2.getStatusCode().value());
        assertEquals(200, response3.getStatusCode().value());
        assertEquals(200, response4.getStatusCode().value());
        assertEquals(200, responseGet.getStatusCode().value());
        assertEquals(2,page.getContent().size());

    }

    @Test
    // # Testa o GET por pesquisa
    // — Cria o token com createToken()
    // — Cria alguns produtos
    // — Cria alguns usuários
    // — Cria algumas recomendações com apenas uma lista de produtos
    // — Faz a chamada na API do ChatGPT para criar as mensagens pra cada recomendação
    // — Faz a chamada na API para retornar todas recomendações do usuário
    // — Verifica se os códigos foram 200
    // — Verifica se as recomendações foram criadas
    // — Verifica se o retorno possuí 1 elementos
    public void givenProdutosAndUsuario_whenGETWithUsuarioId_shouldReturnFromUsuarioId() throws Exception {

        var token = createToken();
        log.info(token.token());

        var pa1 = parceiroRepository.findById(1L)
                .orElseThrow();

        Produto pr1 = Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(pa1).build();
        Produto pr2 = Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(pa1).build();
        Produto pr3 = Produto.builder().nome("Bicicleta de Montanha").tipo("Esporte e Lazer").categoria("Aventura").valor(499.99).descricao("Uma bicicleta resistente projetada para trilhas off-road, com suspensão dianteira e pneus robustos para aventuras na natureza.").parceiro(pa1).recomendacaoList(List.of()).build();
        Produto pr4 = Produto.builder().nome("Máquina de Café Expresso").tipo("Eletrodoméstico").categoria("Culinária").valor(199.99).descricao("Uma máquina de café automática que prepara café expresso delicioso com o toque de um botão, perfeita para os amantes de café.").parceiro(pa1).recomendacaoList(List.of()).build();
        produtoRepository.saveAll(List.of(pr1,pr2,pr3,pr4));

        Usuario u1 = Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build();
        Usuario u2 = Usuario.builder().nome("Mateus Iago Kaique Moreira").cep("64000390").cpf("79528133312").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u3 = Usuario.builder().nome("Pietro Ian Barbosa").cep("66913260").cpf("35789752900").dataNascimento(LocalDate.now()).genero("M").build();
        Usuario u4 = Usuario.builder().nome("Sara Julia Nair Barbosa").cep("65082585").cpf("38665570519").dataNascimento(LocalDate.now()).genero("F").build();
        usuarioRepository.saveAll(List.of(u1,u2,u3,u4));

        Recomendacao r1 = new Recomendacao();
        r1.setProdutoList(List.of(pr1, pr2));
        Recomendacao r2 = new Recomendacao();
        r2.setProdutoList(List.of(pr1, pr3));
        Recomendacao r3 = new Recomendacao();
        r3.setProdutoList(List.of(pr3, pr2));
        Recomendacao r4 = new Recomendacao();
        r4.setProdutoList(List.of(pr4, pr2));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token.token());
        headers.set("Content-Type", "application/json");

        String baseUrl1 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u1.getId();
        String requestBody1 = objectMapper.writeValueAsString(r1);
        HttpEntity<String> requestEntity1 = new HttpEntity<>(requestBody1, headers);
        ResponseEntity<String> response1 = restTemplate.exchange(baseUrl1, HttpMethod.POST, requestEntity1, String.class);

        String baseUrl2 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u2.getId();
        String requestBody2 = objectMapper.writeValueAsString(r2);
        HttpEntity<String> requestEntity2 = new HttpEntity<>(requestBody2, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(baseUrl2, HttpMethod.POST, requestEntity2, String.class);

        String baseUrl3 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u3.getId();
        String requestBody3 = objectMapper.writeValueAsString(r3);
        HttpEntity<String> requestEntity3 = new HttpEntity<>(requestBody3, headers);
        ResponseEntity<String> response3 = restTemplate.exchange(baseUrl3, HttpMethod.POST, requestEntity3, String.class);

        String baseUrl4 = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/"+u4.getId();
        String requestBody4 = objectMapper.writeValueAsString(r4);
        HttpEntity<String> requestEntity4 = new HttpEntity<>(requestBody4, headers);
        ResponseEntity<String> response4 = restTemplate.exchange(baseUrl4, HttpMethod.POST, requestEntity4, String.class);

        HttpHeaders headersGet = new HttpHeaders();
        headersGet.set("Authorization", "Bearer "+token.token());
        HttpEntity<String> requestEntityGet = new HttpEntity<>(headersGet);
        String baseUrlGet = "http://localhost:" + port + "/aishoppingbuddy/api/recomendacao/usuario/3";
        ResponseEntity<CustomPageImpl<Recomendacao>> responseGet = restTemplate.exchange(baseUrlGet, HttpMethod.GET, requestEntityGet, new ParameterizedTypeReference<CustomPageImpl<Recomendacao>>() {});

        PageImpl<Recomendacao> page = responseGet.getBody();
        log.info(page.getContent().toString());

        assertEquals(200, response1.getStatusCode().value());
        assertEquals(200, response2.getStatusCode().value());
        assertEquals(200, response3.getStatusCode().value());
        assertEquals(200, response4.getStatusCode().value());
        assertEquals(200, responseGet.getStatusCode().value());
        assertEquals(1,page.getContent().size());

    }

}
