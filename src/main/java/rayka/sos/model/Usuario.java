package rayka.sos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidade que representa um usuário do sistema")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "ID do Usuário", example = "1")
    private Long uid;

    @UuidGenerator
    private UUID uuid;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nome do Usuário", example = "Rayka")
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    @Schema(description = "E-mail do Usuário", example = "example@gmail.com")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Senha do Usuário", example = "P4ss@241!#")
    private String pass;

    @Lob
    @Column(name = "photo")
    @JdbcTypeCode(Types.LONGVARBINARY) // Diz ao Hibernate para usar o tipo binário grande
    @Schema(description = "Foto de Perfil do Usuário em Bytes")
    private byte[] photo;

    @Column(name = "photo_type", length = 50)
    @Schema(description = "Tipo da Foto de Perfil do Usuário", example = "image/png")
    private String photoType;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cliente> clientes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servico> servicos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemServico> ordens;

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return this.pass;
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
