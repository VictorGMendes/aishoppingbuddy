package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Usuario;
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

@RestController
@RequestMapping("aishoppingbuddy/api/usuario")
public class UsuarioController {
    
    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TokenService tokenService;

    @CrossOrigin
    @GetMapping
    public Page<Usuario> listar(@PageableDefault(size = 5) Pageable pageable) {
        var listUsuario = usuarioRepository.findAll();
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listUsuario.size()));
        return new PageImpl<Usuario>(listUsuario.subList(start, end), pageable, listUsuario.size());
    }

    @GetMapping("{id}")
    public ResponseEntity<Usuario> index(@PathVariable Long id) {
        log.info("buscando usuario " + id);
        var result = usuarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody @Valid Usuario usuario){
        log.info("cadastrando usuario");
        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Usuario> destroy(@PathVariable Long id){
        log.info("deletando usuario " + id);
        var result = usuarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não Encontrado"));
        usuarioRepository.delete(result);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
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
    public Page<Usuario> listar(@PageableDefault(size = 5) Pageable pageable, @PathVariable String busca) {
        var listUsuario = usuarioRepository.findByNomeContainsIgnoreCase(busca);
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), listUsuario.size()));
        return new PageImpl<Usuario>(listUsuario.subList(start, end), pageable, listUsuario.size());
    }

}
