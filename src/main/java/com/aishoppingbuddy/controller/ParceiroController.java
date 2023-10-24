package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Parceiro;
import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Recomendacao;
import com.aishoppingbuddy.model.Transacao;
import com.aishoppingbuddy.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "Bearer Authentication")
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
    @Operation(
            summary = "Listar Parceiros",
            description = "Retorna todos os Parceiros cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parceiros listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public List<Parceiro> load(){
        log.info("exibindo todos parceiros");
        var result = parceiroRepository.findAll();
        log.info("retornando todos parceiros, nº de elementos:"+result.size());
        return result;
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar parceiro",
            description = "Busca um parceiro por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parceiro detalhado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Parceiro com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Parceiro> index(@PathVariable Long id) {
        log.info("buscando parceiro por id:" + id);
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        log.info("encontrado parceiro de id:" + id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(
            summary = "Cadastro parceiro",
            description = "Cadastrar um parceiro pelo id de um Funcionario"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parceiro cadastrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Parceiro com esse ID"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "403", description = "Id do Parceiro inválido")
    })
    public ResponseEntity<Parceiro> create(@RequestBody @Valid Parceiro parceiro){
        log.info("cadastrando parceiro");
        parceiro.setDataEntrada(LocalDate.now());
        parceiroRepository.save(parceiro);
        log.info("cadastrado parceiro: "+parceiro.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(parceiro);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar parceiro",
            description = "Deleta um parceiro por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Parceiro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado parceiro com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Parceiro> destroy(@PathVariable Long id){
        log.info("deletando parceiro de id:" + id);
        log.info("buscando parceiro por id:" + id);
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        log.info("encontrado parceiro de id:" + id);
        parceiroRepository.delete(result);
        log.info("deletado parceiro de id:" + id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Editar parceiro",
            description = "Editar um parceiro por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parceiro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Parceiro com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Parceiro> update(@PathVariable Long id, @RequestBody @Valid Parceiro parceiro){
        log.info("atualizando parceiro de id:"+id);
        log.info("buscando parceiro de id:"+id);
        var result = parceiroRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        log.info("encontrado parceiro de id:"+id);
        parceiro.setId(id);
        parceiro.setTransacaoList(result.getTransacaoList());
        parceiro.setFuncionarioList(result.getFuncionarioList());
        parceiro.setProdutoList(result.getProdutoList());
        parceiro.setRecomendacaoList(result.getRecomendacaoList());
        parceiroRepository.save(parceiro);
        log.info("atualizado parceiro: "+parceiro.toString());
        return ResponseEntity.ok(parceiro);
    }

}
