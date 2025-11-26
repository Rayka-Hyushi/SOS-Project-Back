package rayka.sos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rayka.sos.model.Cliente;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Schema(description = "DTO de resposta para listar/visualizar um ou mais Cliente(s)")
public class ClienteResponseDTO {
    @Schema(description = "UUID do cliente", example = "5d7cxxb2-56xd-4xx5-a1ac-22xx608x4x57")
    private UUID uuid;

    @Schema(description = "Nome do cliente", example = "Rayka")
    private String name;

    @Schema(description = "Telefone do cliente", example = "(55) 91234-5678")
    private String phone;

    @Schema(description = "E-mail do cliente", example = "cliente@example.com")
    private String email;

    @Schema(description = "Endereço do cliente", example = "Rua Um, Bairro, Cidade - UF")
    private String address;

    public ClienteResponseDTO(Cliente cliente) {
        this.uuid = cliente.getUuid();
        this.name = cliente.getName();
        this.phone = cliente.getPhone();
        this.email = cliente.getEmail();
        this.address = cliente.getAddress();
    }
}
