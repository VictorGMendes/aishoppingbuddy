package com.aishoppingbuddy.repository;

import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByParceiro(Parceiro parceiro);

    List<Produto> findByParceiroAndNomeContainsIgnoreCase(Parceiro parceiro, String nome);

}
