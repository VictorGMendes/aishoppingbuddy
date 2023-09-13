package com.aishoppingbuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "T_AISB_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_usuario", nullable = false)
    private long id;

    @Column(name = "nm_usuario", nullable = false)
    private String nome;

    @Column(name = "nr_cpf", nullable = false)
    private String cpf;

    @Column(name = "nr_cep_residencia")
    private String cep;

    @Column(name = "dt_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "ds_genero", nullable = false)
    private String genero;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Recomendacao> recomendacaoList;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Transacao> transacaoList;

}
