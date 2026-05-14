package rayka.sos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "DTO para receber dados de atualização de usuario simples")
public class UsuarioRequestDTO {
    @NotBlank
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres.")
    private String name;

    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    @Size(max = 100, message = "O e-mail não pode ter mais de 100 caracteres.")
    private String email;

    @NotNull
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String pass;

    @Size(max = 500, message = "A URL da foto de perfil não pode ter mais de 500 caracteres.")
    private String profilePhotoUrl;
}
