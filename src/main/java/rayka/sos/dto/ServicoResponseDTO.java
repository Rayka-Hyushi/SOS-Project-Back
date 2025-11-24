package rayka.sos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rayka.sos.model.Servico;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Schema(description = "DTO de referência minimalista para um Serviço")
public class ServicoResponseDTO {
    @Schema(description = "UUID do Serviço", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID uuid;

    @Schema(description = "Nome do Serviço", example = "Troca de Tela")
    private String service;

    @Schema(description = "Descrição do Serviço", example = "Substituição da tela defeituosa")
    private String description;

    @Schema(description = "Valor base do Serviço", example = "80.00")
    private BigDecimal value;

    public ServicoResponseDTO(Servico servico) {
        this.uuid = servico.getUuid();
        this.service = servico.getService();
        this.description = servico.getDescription();
        this.value = servico.getValue();
    }
}
