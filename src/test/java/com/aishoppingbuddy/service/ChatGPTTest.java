package com.aishoppingbuddy.service;

import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Usuario;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChatGPTTest {

    @Autowired
    ChatGPTService chatGPTService;

    @Value("${apiKey}")
    private String apiKey;

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    // # Testa o generateMessage()
    // Cria um parceiro
    // Cria um usuário
    // Cria alguns produtos
    // Gera a mensagem com o generateMessage()
    // Verifica se a mensagem não é nula
    public void givenProdutoListAndUsuario_whenChatGPTService_shouldReturnMessage() throws IOException, InterruptedException {

        Parceiro parceiro1 = Parceiro.builder().nomeFantasia("Amazon").cnpj("38345431000162").dataEntrada(LocalDate.now()).build();
        Usuario u = Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build();

        Produto p1 = Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(parceiro1).recomendacaoList(List.of()).build();
        Produto p2 = Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(parceiro1).recomendacaoList(List.of()).build();

        List<Produto> produtoList = List.of(p1, p2);

        String message = chatGPTService.generateMessage(produtoList,u);

        log.info(message);

        assertNotNull(message);
    }

}
