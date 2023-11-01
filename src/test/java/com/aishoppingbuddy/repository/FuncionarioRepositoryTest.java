package com.aishoppingbuddy.repository;

import com.aishoppingbuddy.model.Funcionario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class FuncionarioRepositoryTest {

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Test
    public void givenFuncionario_whenInsert_shouldBeInDB() throws Exception {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome("nome");
        funcionario.setEmail("email@email.com");
        funcionario.setSenha("senha");
        funcionarioRepository.save(funcionario);

        var encontrado = funcionarioRepository.findById(1L).orElse(null);

        assertThat(encontrado, notNullValue());
        assertThat(encontrado.getNome(), is("nome"));
    }

}
