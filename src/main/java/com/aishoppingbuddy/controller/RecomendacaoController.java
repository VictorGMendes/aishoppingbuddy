package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Produto;
import com.aishoppingbuddy.model.Recomendacao;
import com.aishoppingbuddy.repository.ProdutoRepository;
import com.aishoppingbuddy.repository.RecomendacaoRepository;
import com.aishoppingbuddy.repository.UsuarioRepository;
import com.aishoppingbuddy.service.ChatGPTService;
import com.aishoppingbuddy.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("aishoppingbuddy/api/recomendacao")
@SecurityRequirement(name = "Bearer Authentication")
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

    @Autowired
    ChatGPTService chatGPTService;

    @CrossOrigin
    @GetMapping
    @Operation(
            summary = "Listar recomendações",
            description = "Retorna todos as recomendações"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendações listadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Recomendacao> listar(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable) {
        log.info("exibindo todas recomendacoes");
        log.info("buscando funcionario do token");
        var funcionario = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiro = funcionario.getParceiro();
        log.info("buscando todas recomendacoes");
        var listRecomendacao = recomendacaoRepository.findByParceiro(parceiro);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listRecomendacao.size()));
        log.info("exibindo todas recomendacoes, nº de elementos:"+listRecomendacao.size()+", nº de elementos na pagina:"+listRecomendacao.subList(start, end).size());
        return new PageImpl<Recomendacao>(listRecomendacao.subList(start, end), pageable, listRecomendacao.size());
    }

    @CrossOrigin
    @GetMapping("busca/{busca}")
    @Operation(
            summary = "Listar Recomendações por Busca",
            description = "Recupere uma lista paginada de recomendações com base no critério de busca no título."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendações recuperadas com sucesso."),
            @ApiResponse(responseCode = "403", description = "Token inválido.")
    })
    public Page<Recomendacao> listarBusca(@RequestHeader("Authorization") String header, @PageableDefault(size = 5) Pageable pageable, @PathVariable String busca) {
        log.info("exibindo recomendacoes pela busca: "+busca);
        log.info("buscando funcionario do token");
        var funcionario = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiro = funcionario.getParceiro();
        log.info("buscando recomendacoes pela busca: "+busca);
        var listRecomendacao = recomendacaoRepository.findByParceiroAndTituloContainsIgnoreCase(parceiro,busca);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listRecomendacao.size()));
        log.info("exibindo todas recomendacoes, nº de elementos:"+listRecomendacao.size()+", nº de elementos na pagina:"+listRecomendacao.subList(start, end).size());
        return new PageImpl<Recomendacao>(listRecomendacao.subList(start, end), pageable, listRecomendacao.size());
    }

    @CrossOrigin
    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar Recomendação",
            description = "Busca uma Recomendação por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendação detalhado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Recomendação com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Recomendacao> index(@PathVariable Long id) {
        log.info("buscando recomendacao por id:" + id);
        var result = recomendacaoRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não Encontrado"));
        log.info("encontrado recomendacao por id:" + id);
        return ResponseEntity.ok(result);
    }

    @CrossOrigin
    @PostMapping("{idUsuario}")
    @Operation(
            summary = "Criar Recomendação",
            description = "Cria uma nova recomendação para um usuário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendação criada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Token inválido.")
    })
    public ResponseEntity<Object> criarRecomendacao(@RequestHeader("Authorization") String header, @PathVariable Long idUsuario, @RequestBody @Valid Recomendacao recomendacao) throws IOException, InterruptedException {
        log.info("buscando funcionario do token");
        var funcionarioResult = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiroResult = funcionarioResult.getParceiro();
        log.info("buscando usuario de id:"+idUsuario);
        var usuarioResult = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));

        Optional<Produto> primeiroProduto = produtoRepository.findById(recomendacao.getProdutoList().get(0).getId());

        log.info("gerando mensagem da recomendacao");
        String mensagem = chatGPTService.generateMessage(recomendacao.getProdutoList(),usuarioResult);

        recomendacao.setParceiro(parceiroResult);
        recomendacao.setUsuario(usuarioResult);
        recomendacao.setMensagem(mensagem);
        recomendacao.setData(LocalDate.now());
        primeiroProduto.ifPresent(produto -> recomendacao.setTitulo(produto.getNome()));
        List<Produto> newList = new ArrayList<>();
        for (Produto produto:recomendacao.getProdutoList()) {
            var produtoSalvo = produtoRepository.findById(produto.getId()).orElseThrow();
            newList.add(produtoSalvo);
            log.info("relacionando produto #"+produtoSalvo.getId()+" com a recomendacao criada");
        }
        recomendacao.setProdutoList(newList);
        recomendacaoRepository.save(recomendacao);
        log.info("criada recomendacao: "+recomendacao.toString());

        return ResponseEntity.ok(recomendacaoRepository.findById(recomendacao.getId()));

    }

    @CrossOrigin
    @GetMapping("usuario/{idUsuario}")
    @Operation(
            summary = "Listar Recomendações do Usuário",
            description = "Recupere uma lista paginada de recomendações para um usuário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recomendações recuperadas com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    public Page<Recomendacao> listarUsuario(@RequestHeader("Authorization") String header, @PathVariable long idUsuario, @PageableDefault(size = 5) Pageable pageable) {
        log.info("exibindo recomendacoes do usuario de id:"+idUsuario);
        log.info("buscando funcionario do token");
        var funcionario = tokenService.validate(tokenService.getToken(header));
        log.info("buscando parceiro do funcionario");
        var parceiro = funcionario.getParceiro();
        log.info("buscando recomendacoes do usuario de id:"+idUsuario);
        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        var listRecomendacao = recomendacaoRepository.findByParceiroAndUsuario(parceiro,usuario);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listRecomendacao.size()));
        log.info("exibindo todas recomendacoes, nº de elementos:"+listRecomendacao.size()+", nº de elementos na pagina:"+listRecomendacao.subList(start, end).size());
        return new PageImpl<Recomendacao>(listRecomendacao.subList(start, end), pageable, listRecomendacao.size());
    }

}
