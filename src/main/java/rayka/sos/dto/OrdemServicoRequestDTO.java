package rayka.sos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import rayka.sos.model.StatusOrdemServico;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO para receber os dados da Ordem de Serviço (Requisição de POST/PUT)")
public class OrdemServicoRequestDTO {
    @NotNull(message = "O UUID do cliente é obrigatório.")
    @Schema(description = "UUID do cliente ao qual a Ordem de Serviço pertence.",
        example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID clienteUuid;

    @NotBlank(message = "O nome do dispositivo é obrigatório.")
    @Size(min = 3, max = 50, message = "O nome do dispositivo deve ter entre 3 e 50 caracteres.")
    @Schema(description = "Nome/Modelo do dispositivo a ser reparado.", example = "Notebook Dell Inspiron 15")
    private String device;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 500, message = "A descrição fornecida é longa demais. Reduza a descrição para no máximo 500 caracteres.")
    @Schema(description = "Detalhes do problema ou serviço requisitado.", example = "Tela quebrada, não liga após queda.")
    private String description;

    @NotNull(message = "A lista de UUIDs dos serviços é obrigatória.")
    @Schema(description = "Lista de UUIDs dos serviços que compõem a Ordem de Serviço.")
    private List<UUID> servicosUuids;

    @NotNull(message = "O status é obrigatório.")
    @Schema(description = "Status atual da Ordem de Serviço.", example = "Em Andamento", implementation = StatusOrdemServico.class)
    private StatusOrdemServico status;

    @NotNull(message = "O valor de extras é obrigatório.")
    @Digits(integer = 4, fraction = 2, message = "O valor de extras deve ter no maximo 4 dígitos inteiros e 2 casas decimais.")
    @Schema(description = "Custo adicional por peças ou extras (ex: valor de taxa).", example = "50.00")
    private BigDecimal extras;

    @NotNull(message = "O valor de desconto é obrigatório.")
    @Digits(integer = 4, fraction = 2, message = "O valor de desconto deve ter no maximo 4 dígitos inteiros e 2 casas decimais.")
    @Schema(description = "Valor total de desconto aplicado à Ordem de Serviço.", example = "10.00")
    private BigDecimal discount;
}
