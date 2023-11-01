package com.aishoppingbuddy;

import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.repository.ParceiroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootApplication
public class AiShoppingBuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiShoppingBuddyApplication.class, args);
	}

	Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	@Transactional
	public CommandLineRunner demo(ParceiroRepository parceiroRepository) {
		return (args) -> {

			var parceiro = new Parceiro();
			parceiro.setNomeFantasia("AiShoppingBuddy");
			parceiro.setCnpj("70898958000127");
			parceiro.setDataEntrada(LocalDate.now());

			parceiroRepository.save(parceiro);
			parceiroRepository.flush();
			log.info(parceiroRepository.findAll().toString());

		};
	}

}
