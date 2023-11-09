package com.aishoppingbuddy.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity(name = "t_aisb_recomendacao")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recomendacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_recomendacao", nullable = false)
    private long id;

    @Column(name = "ds_titulo", nullable = false)
    private String titulo;

    @Column(name = "ds_mensagem_recomendacao", nullable = false,length = 2000)
    private String mensagem;

    @Column(name = "dt_mensagem", nullable = false)
    private LocalDate data;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "possui",
            joinColumns = @JoinColumn(name = "cd_recomendacao"),
            inverseJoinColumns = @JoinColumn(name = "cd_produto"))
    private List<Produto> produtoList;

    @ManyToOne
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "cd_parceiro")
    private Parceiro parceiro;

}
