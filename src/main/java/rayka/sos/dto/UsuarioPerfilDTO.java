package rayka.sos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import rayka.sos.model.Usuario;

import java.util.Base64;
import java.util.UUID;

@Getter
@Schema(description = "DTO de Resposta para o Perfil do Usuário (Dados de exibição)")
public class UsuarioPerfilDTO {
    @Schema(description = "UUID único do usuário", example = "5d7caeb2-56ed-4cd5-a1ac-22cd608f4157")
    private UUID uuid;

    @Schema(description = "Nome completo do usuário", example = "Rayka Hyushi")
    private String name;

    @Schema(description = "E-mail de login do usuário", example = "admin@admin.com")
    private String email;

    @Schema(description = "Foto de perfil do usuário codificada em Base64. Pode ser nula.", example = "iVBORw0KAAAhEUgA...")
    private String photo;

    @Schema(description = "Tipo da foto de perfil do usuário", example = "image/png")
    private String photoType;

    public UsuarioPerfilDTO(Usuario usuario) {
        this.uuid = usuario.getUuid();
        this.name = usuario.getName();
        this.email = usuario.getEmail();

        if (usuario.getPhoto() != null) {
            this.photo = Base64.getEncoder().encodeToString(usuario.getPhoto());
            this.photoType = usuario.getPhotoType();
        } else {
            this.photo = null;
        }
    }
}
