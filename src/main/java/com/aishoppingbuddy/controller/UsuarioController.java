package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Usuario;
import com.aishoppingbuddy.repository.UsuarioRepository;
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

@RestController
@RequestMapping("aishoppingbuddy/api/usuario")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {
    
    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TokenService tokenService;

    @CrossOrigin
    @GetMapping
    @Operation(
            summary = "Listar Usuários",
            description = "Retorna todos os Usuários cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Usuario> listar(@PageableDefault(size = 5) Pageable pageable) {
        var listUsuario = usuarioRepository.findAll();
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listUsuario.size()));
        return new PageImpl<Usuario>(listUsuario.subList(start, end), pageable, listUsuario.size());
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar usuário",
            description = "Busca um usuário por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário detalhado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Usuário com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Usuario> index(@PathVariable Long id) {
        log.info("buscando usuario " + id);
        var result = usuarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(
            summary = "Criar Usuário",
            description = "Cria um novo usuário."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Usuario> create(@RequestBody @Valid Usuario usuario){
        log.info("cadastrando usuario");
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar usuario",
            description = "Deleta um usuario por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "usuario deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado usuario com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Usuario> destroy(@PathVariable Long id){
        log.info("deletando usuario " + id);
        var result = usuarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        usuarioRepository.delete(result);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Editar usuario",
            description = "Editar um usuario por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Usuario com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody @Valid Usuario usuario){
        log.info("atualizando usuario "+id);
        var result = usuarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        usuario.setId(id);
        usuario.setRecomendacaoList(result.getRecomendacaoList());
        usuario.setTransacaoList(result.getTransacaoList());
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("nome/{busca}")
    @Operation(
            summary = "Listar Usuários por Nome",
            description = "Recupere uma lista paginada de usuários com base no critério de busca por nome."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários recuperados com sucesso."),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public Page<Usuario> listar(@PageableDefault(size = 5) Pageable pageable, @PathVariable String busca) {
        var listUsuario = usuarioRepository.findByNomeContainsIgnoreCase(busca);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listUsuario.size()));
        return new PageImpl<Usuario>(listUsuario.subList(start, end), pageable, listUsuario.size());
    }

}
