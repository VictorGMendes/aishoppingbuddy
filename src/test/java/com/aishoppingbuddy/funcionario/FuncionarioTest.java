package com.aishoppingbuddy.funcionario;

import com.aishoppingbuddy.AiShoppingBuddyApplication;
import com.aishoppingbuddy.controller.FuncionarioController;
import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.service.AuthenticationService;
import com.aishoppingbuddy.service.TokenService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(FuncionarioController.class)
public class FuncionarioTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager manager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private FuncionarioRepository funcionarioRepository;

    @MockBean
    private ParceiroRepository parceiroRepository;

    @Test
    public void givenFuncionario_whenPost_thenShouldBeRegistered() throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8080/aishoppingbuddy/api/funcionario/cadastrar/1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Parceiro parceiro = Parceiro.builder().nomeFantasia("Amazon").cnpj("38345431000162").dataEntrada(LocalDate.now()).build();
        parceiroRepository.save(parceiro);

        String requestBody = String.format("{ \"nome\":\"%1$s\", \"email\":\"%2$s\", \"senha\":\"%3$s\" }","Funcionario Teste","teste@email.com",encoder.encode("senhateste"));

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        //Assert.assertEquals(200,response.getStatusCode());


        mvc.perform(MockMvcRequestBuilders.post("/aishoppingbuddy/api/funcionario/cadastrar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .sessionAttrs(null))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }

}
