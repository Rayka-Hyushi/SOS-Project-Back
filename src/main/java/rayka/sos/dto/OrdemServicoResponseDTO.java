package rayka.sos.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rayka.sos.model.OrdemServico;
import rayka.sos.model.Servico;
import rayka.sos.model.StatusOrdemServico;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Schema(description = "DTO de Resposta para Ordens de Serviço (Dados completos e seguros da OS)")
public class OrdemServicoResponseDTO {
    @Schema(description = "UUID da ordem")
    private UUID uuid;

    @Schema(description = "UUID do cliente ao qual a Ordem de Serviço está vinculada.",
        example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private UUID clienteUuid;

    @Schema(description = "Nome/Modelo do dispositivo em serviço.",
        example = "Notebook Dell Inspiron 15")
    private String device;

    @Schema(description = "Descrição detalhada do problema ou serviço.",
        example = "Reparo na tela e otimização de software.")
    private String description;

    @ArraySchema(schema = @Schema(implementation = Servico.class, description = "Conjunto de serviços incluídos na Ordem de Serviço."))
    private Set<ServicoResponseDTO> servicos;

    @Schema(description = "Status atual da Ordem de Serviço.", example = "Em Andamento", implementation = StatusOrdemServico.class)
    private StatusOrdemServico status;

    @Schema(description = "Valor adicional por peças, taxa de urgência ou outros extras.", example = "150.50")
    private BigDecimal extras;

    @Schema(description = "Valor total de desconto aplicado.", example = "20.00")
    private BigDecimal discount;

    @Schema(description = "Valor total final da Ordem de Serviço (Calculado).", example = "830.50")
    private BigDecimal total;

    @Schema(description = "Referência ao usuário dono do registro.")
    private UsuarioReferenciaDTO usuario;

    public OrdemServicoResponseDTO(OrdemServico os) {
        this.uuid = os.getUuid();
        this.clienteUuid = os.getCliente().getUuid();
        this.device = os.getDevice();
        this.description = os.getDescription();

        this.servicos = os.getServicos().stream()
            .map(ServicoResponseDTO::new)
            .collect(Collectors.toSet());

        this.status = os.getStatus();
        this.extras = os.getExtras();
        this.discount = os.getDiscount();
        this.total = os.getTotal();
        this.usuario = new UsuarioReferenciaDTO(os.getUsuario());
    }
}
