package com.aishoppingbuddy.model;

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
@Entity(name = "T_AISB_TRANSACAO")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_transacao", nullable = false)
    private long id;

    @Column(name = "st_cancelado", nullable = false)
    private boolean cancelado;

    @Column(name = "vl_total")
    private double valorTotal;

    @Column(name = "nr_cep_estabelecimento")
    private String cep;

    @Column(name = "dt_transacao", nullable = false)
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "cd_parceiro")
    private Parceiro parceiro;

    @OneToMany(mappedBy = "transacao")
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Produto> produtoList;

}
