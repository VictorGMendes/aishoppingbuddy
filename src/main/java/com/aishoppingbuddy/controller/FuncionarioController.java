package com.aishoppingbuddy.controller;

import com.aishoppingbuddy.model.Credencial;
import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.aishoppingbuddy.repository.ParceiroRepository;
import com.aishoppingbuddy.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("aishoppingbuddy/api/funcionario")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Funcionário", description = "Funcionário de um Parceiro")
public class FuncionarioController {
    
    Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    ParceiroRepository parceiroRepository;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;
    
    @GetMapping
    @Operation(
            summary = "Listar funcionários",
            description = "Retorna todos os funcionários cadastrados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionários listados com sucesso"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public List<Funcionario> load() {
        log.info("exibindo todos funcionarios");
        var result = funcionarioRepository.findAll();
        log.info("retornando todos funcionarios, nº de elementos:"+result.size());
        return result;
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Detalhar funcionário",
            description = "Busca um funcionário por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário detalhado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Funcionário com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Funcionario> index(@PathVariable Long id) {
        log.info("buscando funcionario por id: " + id);
        var result = funcionarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario não Encontrado"));
        log.info("encontrado funcionario de id: " + id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("cadastrar/{idParceiro}")
    @Operation(
            summary = "Cadastro funcionário",
            description = "Cadastra um funcionário pelo id de um Parceiro"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Parceiro com esse ID"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "403", description = "Id do Parceiro inválido")
    })
    public ResponseEntity<Object> cadastro(@PathVariable Long idParceiro, @RequestBody @Valid Funcionario funcionario) {
        log.info("cadastrando funcionario");
        log.info(parceiroRepository.findAll().toString());
        log.info("buscando parceiro de id: "+idParceiro);
        var parceiroResult = parceiroRepository.findById(idParceiro)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Parceiro não Encontrado"));
        log.info("encontrado parceiro de id: "+parceiroResult.getId());
        funcionario.setParceiro(parceiroResult);
        funcionario.setSenha(encoder.encode(funcionario.getSenha()));
        funcionarioRepository.save(funcionario);
        log.info("cadastrado funcionario: "+funcionario.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario);
    }

    @CrossOrigin
    @PostMapping("login")
    @Operation(
            summary = "Login de funcionário",
            description = "Realiza o login de um funcionário"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Dados inválidos")
    })
    public ResponseEntity<Object> login(@RequestBody Credencial credencial) {
        manager.authenticate(credencial.toAuthentication());
        log.info("credenciais autenticadas");
        var token = tokenService.generateToken(credencial);
        log.info("gerado token");
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Deletar funcionário",
            description = "Deleta um funcionário por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Funcionário com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Funcionario> destroy(@PathVariable Long id){
        log.info("deletando funcionario de id: "+id);
        log.info("buscando funcionario de id: " + id);
        var result = funcionarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario não Encontrado"));
        log.info("encontrado funcionario de id: " + id);
        funcionarioRepository.delete(result);
        log.info("deletado funcionario de id: " + id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Editar funcionário",
            description = "Editar um funcionário por id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "404", description = "Não foi encontrado Funcionário com esse ID"),
            @ApiResponse(responseCode = "403", description = "Token inválido")
    })
    public ResponseEntity<Funcionario> update(@PathVariable Long id, @RequestBody @Valid Funcionario funcionario){
        log.info("atualizando funcionario de id: "+id);
        log.info("buscando funcionario de id: "+id);
        var result = funcionarioRepository.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario não Encontrado"));
        log.info("encontrado funcionario de id: "+id);
        funcionario.setSenha(encoder.encode(funcionario.getPassword()));
        funcionario.setId(id);
        funcionario.setParceiro(result.getParceiro());
        funcionarioRepository.save(funcionario);
        log.info("atualizado funcionario: "+funcionario.toString());
        return ResponseEntity.ok(funcionario);
    }
    
}
