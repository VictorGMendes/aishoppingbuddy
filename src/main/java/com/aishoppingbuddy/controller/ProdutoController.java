package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.repository.ProdutoRepository;
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

@RestController
@RequestMapping("aishoppingbuddy/api/produto")
public class ProdutoController {
    
    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    ParceiroRepository parceiroRepository;

    @Autowired
    TokenService tokenService;
    
    @GetMapping
    public Page<Produto> load(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable) {
        log.info("buscando funcionario");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        var parceiroResult = funcionarioResult.getParceiro();
        var listProduto = produtoRepository.findByParceiro(parceiroResult);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listProduto.size()));
        return new PageImpl<Produto>(listProduto.subList(start, end), pageable, listProduto.size());
    }
    
    @GetMapping("nome/{busca}")
    public Page<Produto> listar(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable, @PathVariable String busca) {
        log.info("buscando funcionario");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        var parceiroResult = funcionarioResult.getParceiro();
        var listProduto = produtoRepository.findByParceiroAndNomeContainsIgnoreCase(parceiroResult, busca);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listProduto.size()));
        return new PageImpl<Produto>(listProduto.subList(start, end), pageable, listProduto.size());
    }

    @GetMapping("{id}")
    public ResponseEntity<Produto> index(@PathVariable Long id) {
        log.info("buscando produto " + id);
        var result = produtoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Produto> create(@RequestHeader("Authorization") String header, @RequestBody @Valid Produto produto) {
        log.info("cadastrando produto");
        log.info("buscando funcionario");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        var parceiroResult = funcionarioResult.getParceiro();
        produto.setParceiro(parceiroResult);
        produtoRepository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Produto> destroy(@PathVariable Long id){
        log.info("deletando produto " + id);
        var result = produtoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não Encontrado"));
        produtoRepository.delete(result);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Produto> update(@PathVariable Long id, @RequestBody @Valid Produto produto){
        log.info("atualizando produto "+id);
        var result = produtoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não Encontrado"));
        produto.setId(id);
        produto.setParceiro(result.getParceiro());
        produto.setRecomendacaoList(result.getRecomendacaoList());
        produto.setTransacao(result.getTransacao());
        produtoRepository.save(produto);
        return ResponseEntity.ok(produto);
    }
    
}
