package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Recomendacao;
import com.aishoppingbuddy.model.Transacao;
import com.aishoppingbuddy.repository.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("aishoppingbuddy/api/parceiro")
public class ParceiroController {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ParceiroRepository parceiroRepository;

    @Autowired
    TransacaoRepository transacaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    RecomendacaoRepository recomendacaoRepository;

    @GetMapping
    public List<Parceiro> load(){
        return parceiroRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Parceiro> index(@PathVariable Long id) {
        log.info("buscando parceiro " + id);
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Parceiro> create(@RequestBody @Valid Parceiro parceiro){
        log.info("cadastrando parceiro");
        parceiro.setDataEntrada(LocalDate.now());
        parceiroRepository.save(parceiro);
        return ResponseEntity.status(HttpStatus.CREATED).body(parceiro);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Parceiro> destroy(@PathVariable Long id){
        log.info("deletando parceiro " + id);
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        parceiroRepository.delete(result);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Parceiro> update(@PathVariable Long id, @RequestBody @Valid Parceiro parceiro){
        log.info("atualizando parceiro "+id);
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        parceiro.setId(id);
        parceiro.setTransacaoList(result.getTransacaoList());
        parceiro.setFuncionarioList(result.getFuncionarioList());
        parceiro.setProdutoList(result.getProdutoList());
        parceiro.setRecomendacaoList(result.getRecomendacaoList());
        parceiroRepository.save(parceiro);
        return ResponseEntity.ok(parceiro);
    }

    // Não estamos usando os endpoints de transações, pois eles apenas serviam para o ML.

    @Deprecated
    @PostMapping("{id}/transacoes")
    public ResponseEntity<Parceiro> cadastrarTransacoes(@PathVariable Long id, @RequestBody List<Transacao> transacaoList) {
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        for (Transacao t :
                transacaoList) {
            t.setParceiro(result);
            usuarioRepository.save(t.getUsuario());
            produtoRepository.saveAll(t.getProdutoList());
            transacaoRepository.save(t);
            log.info("cadastrando transação "+t.getId()+" no parceiro "+result.getId());
        }
        return ResponseEntity.ok(result);
    }
    @Deprecated
    @GetMapping("{id}/transacoes")
    public List<Transacao> listarTransacoes(@PathVariable Long id) {
        var parceiroResult = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        return transacaoRepository.findAll();
    }
    @Deprecated
    @PostMapping("{idParceiro}/recomendacoes/{idUsuario}")
    public ResponseEntity<Recomendacao> criarRecomendacao(@PathVariable Long idParceiro, @PathVariable Long idUsuario, @RequestBody List<Transacao> transacaoList) {
        var parceiroResult = parceiroRepository.findById(idParceiro)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        var usuarioResult = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));

        // DADOS GERADOS PELA API DE MACHINE LEARNING
        String mensagem = "MENSAGEM GERADA PELO CHATGPT";
        LocalDate data = LocalDate.now();
        List<Produto> produtoList = List.of(
                Produto.builder()
                        .nome("NOME DO PRODUTO RECOMENDADO PELO MACHINE LEARNING")
                        .tipo("TIPO DO PRODUTO RECOMENDADO PELO MACHINE LEARNING")
                        .valor(50)
                        .descricao("DESCRICAO DO PRODUTO RECOMENDADO PELO MACHINE LEARNING")
                        .categoria("CATEGORIA DO PRODUTO RECOMENDADO PELO MACHINE LEARNING")
                        .build()
        );
        produtoRepository.saveAll(produtoList);
        log.info("recuperado dados da api");

        Recomendacao recomendacao = Recomendacao
                .builder()
                .mensagem(mensagem)
                .data(data)
                .parceiro(parceiroResult)
                .usuario(usuarioResult)
                .produtoList(produtoList)
                .build();

        recomendacaoRepository.save(recomendacao);
        log.info("recomendacao "+recomendacao.getId()+" salva");
        return ResponseEntity.ok(recomendacao);
    }

    @Deprecated
    @GetMapping("{id}/recomendacoes")
    public List<Recomendacao> listarTodasRecomendacoes(@PathVariable Long id) {
        var parceiroResult = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        return recomendacaoRepository.findByParceiro(parceiroResult);
    }

    @Deprecated
    @GetMapping("{idParceiro}/recomendacoes/usuario/{idUsuario}")
    public List<Recomendacao> listarRecomendacoesUsuario(@PathVariable Long idParceiro, @PathVariable Long idUsuario) {
        var parceiroResult = parceiroRepository.findById(idParceiro)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        var usuarioResult = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        return recomendacaoRepository.findByParceiroAndUsuario(parceiroResult, usuarioResult);
    }

    @Deprecated
    @GetMapping("{idParceiro}/recomendacoes/data/{data}")
    public List<Recomendacao> listarRecomendacoesData(@PathVariable Long idParceiro, @PathVariable LocalDate data) {
        var parceiroResult = parceiroRepository.findById(idParceiro)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        return recomendacaoRepository.findByParceiroAndData(parceiroResult,data);
    }

    @Deprecated
    @GetMapping("{idParceiro}/recomendacoes/{idRecomendacao}")
    public Recomendacao acharRecomendacao(@PathVariable Long idParceiro, @PathVariable Long idRecomendacao) {
        var parceiroResult = parceiroRepository.findById(idParceiro)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        var recomendacaoResult = recomendacaoRepository.findById(idRecomendacao)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não Encontrado"));
        if (recomendacaoResult.getParceiro() != parceiroResult) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Essa recomendação é de outro Parceiro");
        }
        return recomendacaoResult;
    }

}
