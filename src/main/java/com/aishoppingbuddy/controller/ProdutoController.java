package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.repository.ProdutoRepository;
import com.aishoppingbuddy.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "Bearer Authentication")
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
        log.info("exibindo todos produtos");
        log.info("buscando funcionario do token");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiroResult = funcionarioResult.getParceiro();
        log.info("buscando todos produtos");
        var listProduto = produtoRepository.findByParceiro(parceiroResult);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listProduto.size()));
        log.info("exibindo todos produtos, nº de elementos:"+listProduto.size()+", nº de elementos na pagina:"+listProduto.subList(start, end).size());
        return new PageImpl<Produto>(listProduto.subList(start, end), pageable, listProduto.size());
    }
    
    @GetMapping("nome/{busca}")
    public Page<Produto> listar(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable, @PathVariable String busca) {
        log.info("exibindo produtos pelo nome: "+busca);
        log.info("buscando funcionario do token");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiroResult = funcionarioResult.getParceiro();
        log.info("buscando produtos pelo nome: "+busca);
        var listProduto = produtoRepository.findByParceiroAndNomeContainsIgnoreCase(parceiroResult, busca);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listProduto.size()));
        log.info("exibindo todos produtos, nº de elementos:"+listProduto.size()+", nº de elementos na pagina:"+listProduto.subList(start, end).size());
        return new PageImpl<Produto>(listProduto.subList(start, end), pageable, listProduto.size());
    }

    @GetMapping("{id}")
    public ResponseEntity<Produto> index(@PathVariable Long id) {
        log.info("buscando produto por id:" + id);
        var result = produtoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não Encontrado"));
        log.info("encontrado produto de id:"+id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Produto> create(@RequestHeader("Authorization") String header, @RequestBody @Valid Produto produto) {
        log.info("cadastrando produto");
        log.info("buscando funcionario do token");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiroResult = funcionarioResult.getParceiro();
        produto.setParceiro(parceiroResult);
        produtoRepository.save(produto);
        log.info("cadastrado produto: "+produto.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Produto> destroy(@PathVariable Long id){
        log.info("deletando produto de id:" + id);
        log.info("buscando produto por id:" + id);
        var result = produtoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não Encontrado"));
        log.info("encontrado produto de id:"+id);
        produtoRepository.delete(result);
        log.info("deletado produto de id:"+id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Produto> update(@PathVariable Long id, @RequestBody @Valid Produto produto){
        log.info("atualizando produto "+id);
        log.info("buscando produto por id:" + id);
        var result = produtoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não Encontrado"));
        log.info("encontrado produto de id:"+id);
        produto.setId(id);
        produto.setParceiro(result.getParceiro());
        produto.setRecomendacaoList(result.getRecomendacaoList());
        produto.setTransacao(result.getTransacao());
        produtoRepository.save(produto);
        log.info("atualizado produto: "+produto.toString());
        return ResponseEntity.ok(produto);
    }
    
}
