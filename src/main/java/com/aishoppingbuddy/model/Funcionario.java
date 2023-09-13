package com.aishoppingbuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "T_AISB_FUNCIONARIO")
public class Funcionario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_funcionario", nullable = false)
    private long id;

    @Column(name = "nm_funcionario", nullable = false)
    private String nome;

    @Column(name = "ds_email", nullable = false)
    @NotEmpty(message = "Email é obrigatório.")
    @Email(message = "O Email precisa ser válido")
    private String email;

    @Column(name = "ds_senha", nullable = false)
    @NotEmpty(message = "Senha é obrigatório.")
    private String senha;

    @ManyToOne
    @JoinColumn(name = "cd_parceiros")
    private Parceiro parceiro;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
