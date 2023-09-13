package com.aishoppingbuddy.repository;

import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Recomendacao;
import com.aishoppingbuddy.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecomendacaoRepository extends JpaRepository<Recomendacao, Long> {

    List<Recomendacao> findByParceiro(Parceiro parceiro);

    List<Recomendacao> findByParceiroAndTituloContainsIgnoreCase(Parceiro parceiro, String titulo);

    List<Recomendacao> findByParceiroAndUsuario(Parceiro parceiro, Usuario usuario);

    List<Recomendacao> findByParceiroAndData(Parceiro parceiro, LocalDate data);

}
