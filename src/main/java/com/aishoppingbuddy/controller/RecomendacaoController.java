package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Recomendacao;
import com.aishoppingbuddy.repository.ProdutoRepository;
import com.aishoppingbuddy.repository.RecomendacaoRepository;
import com.aishoppingbuddy.repository.UsuarioRepository;
import com.aishoppingbuddy.service.TokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("aishoppingbuddy/api/recomendacao")
public class RecomendacaoController {

    @Autowired
    RecomendacaoRepository recomendacaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TokenService tokenService;

    @CrossOrigin
    @GetMapping
    public Page<Recomendacao> listar(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable) {
        log.info("buscando funcionario");
        var funcionario = tokenService.validate(tokenService.getToken(header));
        var parceiro = funcionario.getParceiro();
        var listRecomendacao = recomendacaoRepository.findByParceiro(parceiro);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listRecomendacao.size()));
        return new PageImpl<Recomendacao>(listRecomendacao.subList(start, end), pageable, listRecomendacao.size());
    }

    @CrossOrigin
    @GetMapping("busca/{busca}")
    public Page<Recomendacao> listarBusca(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable, @PathVariable String busca) {
        log.info("buscando funcionario");
        var funcionario = tokenService.validate(tokenService.getToken(header));
        var parceiro = funcionario.getParceiro();
        var listRecomendacao = recomendacaoRepository.findByParceiroAndTituloContainsIgnoreCase(parceiro,busca);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listRecomendacao.size()));
        return new PageImpl<Recomendacao>(listRecomendacao.subList(start, end), pageable, listRecomendacao.size());
    }

    @CrossOrigin
    @GetMapping("{id}")
    public ResponseEntity<Recomendacao> index(@PathVariable Long id) {
        log.info("buscando recomendacao " + id);
        var result = recomendacaoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @CrossOrigin
    @PostMapping("{idUsuario}")
    public ResponseEntity<Object> criarRecomendacao(@RequestHeader("Authorization") String header, @PathVariable Long idUsuario, @RequestBody @Valid Recomendacao recomendacao) {

        log.info("buscando usuario");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        var parceiroResult = funcionarioResult.getParceiro();
        var usuarioResult = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));

        Optional<Produto> primeiroProduto = produtoRepository.findById(recomendacao.getProdutoList().get(0).getId());

        String mensagem = "MENSAGEM GERADA PELO CHATGPT";

        recomendacao.setParceiro(parceiroResult);
        recomendacao.setUsuario(usuarioResult);
        recomendacao.setMensagem(mensagem);
        recomendacao.setData(LocalDate.now());
        primeiroProduto.ifPresent(produto -> recomendacao.setTitulo(produto.getNome()));

        recomendacaoRepository.save(recomendacao);
        log.info("recomendacao "+recomendacao.getId()+" salva");

        for (Produto produto:recomendacao.getProdutoList()) {
            Produto produtoSalvo = produtoRepository.getReferenceById(produto.getId());
            produtoSalvo.getRecomendacaoList().add(recomendacao);
            produtoRepository.save(produtoSalvo);
            log.info("salvando produto "+produtoSalvo.getId()+" na recomendacao "+recomendacao.getId());
        }

        return ResponseEntity.ok(recomendacaoRepository.findById(recomendacao.getId()));

    }

    @CrossOrigin
    @GetMapping("usuario/{idUsuario}")
    public Page<Recomendacao> listarUsuario(@RequestHeader("Authorization") String header, @PathVariable long idUsuario, @PageableDefault(size = 5) Pageable pageable) {
        log.info("buscando funcionario");
        var funcionario = tokenService.validate(tokenService.getToken(header));
        var parceiro = funcionario.getParceiro();
        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        var listRecomendacao = recomendacaoRepository.findByParceiroAndUsuario(parceiro,usuario);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listRecomendacao.size()));
        return new PageImpl<Recomendacao>(listRecomendacao.subList(start, end), pageable, listRecomendacao.size());
    }

}
