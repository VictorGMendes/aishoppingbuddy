package com.aishoppingbuddy.config;

import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Usuario;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.repository.ProdutoRepository;
import com.aishoppingbuddy.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    ParceiroRepository parceiroRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {

        Parceiro parceiro1 = Parceiro.builder().nomeFantasia("Amazon").cnpj("38345431000162").dataEntrada(LocalDate.now()).build();
        Parceiro parceiro2 = Parceiro.builder().nomeFantasia("Mercado Livre").cnpj("27303425000126").dataEntrada(LocalDate.now()).build();
        // Parceiro parceiro3 = Parceiro.builder().nomeFantasia("nome3").cnpj("cnpj3").dataEntrada(LocalDate.now()).build();
        // Parceiro parceiro4 = Parceiro.builder().nomeFantasia("nome4").cnpj("cnpj4").dataEntrada(LocalDate.now()).build();

        parceiroRepository.deleteAll();
        parceiroRepository.saveAll(List.of(
                parceiro1, parceiro2
        ));

        funcionarioRepository.saveAll(List.of(
                Funcionario.builder().nome("Yago Leandro Martin Lima").email("yago-lima87@doublemoore.com").senha(encoder.encode("CatGlRIxYm")).parceiro(parceiro1).build(),
                Funcionario.builder().nome("Stella Ayla Dias").email("stella.ayla.dias@helpvale.com.br").senha(encoder.encode("AYE3s0xku2")).parceiro(parceiro2).build()
        ));

        usuarioRepository.deleteAll();
        usuarioRepository.saveAll(List.of(
                Usuario.builder().nome("Sandra Cristiane Sophie Monteiro").cep("97543160").cpf("19265516054").dataNascimento(LocalDate.now()).genero("F").build(),
                Usuario.builder().nome("Mateus Iago Kaique Moreira").cep("64000390").cpf("79528133312").dataNascimento(LocalDate.now()).genero("M").build(),
                Usuario.builder().nome("Pietro Ian Barbosa").cep("66913260").cpf("35789752900").dataNascimento(LocalDate.now()).genero("M").build(),
                Usuario.builder().nome("Sara Julia Nair Barbosa").cep("65082585").cpf("38665570519").dataNascimento(LocalDate.now()).genero("F").build()
        ));

        produtoRepository.deleteAll();
        produtoRepository.saveAll(List.of(
                Produto.builder().nome("Smartphone Galaxy S21").tipo("Eletrônico").categoria("Tecnologia").valor(999.99).descricao("Um smartphone de última geração com tela AMOLED de 6,2 polegadas, câmera de alta resolução e processador poderoso.").parceiro(parceiro1).recomendacaoList(List.of()).build(),
                Produto.builder().nome("Livro: O Senhor dos Anéis").tipo("Livro").categoria("Literatura").valor(29.99).descricao("Uma obra épica de fantasia que narra a jornada de Frodo Baggins para destruir o Um Anel e salvar a Terra-média.").parceiro(parceiro1).recomendacaoList(List.of()).build(),
                Produto.builder().nome("Bicicleta de Montanha").tipo("Esporte e Lazer").categoria("Aventura").valor(499.99).descricao("Uma bicicleta resistente projetada para trilhas off-road, com suspensão dianteira e pneus robustos para aventuras na natureza.").parceiro(parceiro2).recomendacaoList(List.of()).build(),
                Produto.builder().nome("Máquina de Café Expresso").tipo("Eletrodoméstico").categoria("Culinária").valor(199.99).descricao("Uma máquina de café automática que prepara café expresso delicioso com o toque de um botão, perfeita para os amantes de café.").parceiro(parceiro2).recomendacaoList(List.of()).build()
        ));
    }

}
