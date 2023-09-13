package com.aishoppingbuddy.service;

import com.aishoppingbuddy.model.Credencial;
import com.aishoppingbuddy.model.Funcionario;
import com.aishoppingbuddy.model.Token;
import com.aishoppingbuddy.repository.FuncionarioRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Autowired
    FuncionarioRepository funcionarioRepository;

    public Token generateToken(Credencial credencial) {
        Algorithm alg = Algorithm.HMAC256("meusecret");
        var jwt = JWT.create()
                .withSubject(credencial.email())
                .withIssuer("AIShoppingBuddy")
                .withExpiresAt(Instant.now().plus(4, ChronoUnit.HOURS))
                .sign(alg);
        return new Token(jwt, "JWT", "Bearer");
    }

    public Funcionario validate(String token) {
        Algorithm alg = Algorithm.HMAC256("meusecret");
        var email = JWT.require(alg)
                .withIssuer("AIShoppingBuddy")
                .build()
                .verify(token)
                .getSubject();

        return (Funcionario) funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new JWTVerificationException("usuario não encontrado"));
    }

    public String getToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.replace("Bearer ", "");
    }

}
