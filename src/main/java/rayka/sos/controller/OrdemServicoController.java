package rayka.sos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rayka.sos.dto.OrdemServicoRequestDTO;
import rayka.sos.dto.OrdemServicoResponseDTO;
import rayka.sos.model.OrdemServico;
import rayka.sos.model.Usuario;
import rayka.sos.service.OrdemServicoService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordens")
@Tag(name = "Ordens", description = "Path relacionado a operações de Ordens de Serviço")
public class OrdemServicoController {
    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Operation(summary = "Criar Ordem de Serviço", description = "Cadastra uma nova OS, associada ao usuário logado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "OS criada com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = OrdemServico.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida (Dados ausentes ou Cliente/Serviço não encontrados)"),
        @ApiResponse(responseCode = "403", description = "Não autorizado")
    })
    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> criarOrdem(@RequestBody OrdemServicoRequestDTO osrDTO) {
        OrdemServico salvo = ordemServicoService.create(osrDTO, getUsuarioLogado());
        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(salvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Listar Ordens de Serviço", description = "Retorna todas as OS's do usuário logado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de OS's retornada com sucesso",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = OrdemServico.class)))),
        @ApiResponse(responseCode = "403", description = "Não autorizado")
    })
    @GetMapping
    public ResponseEntity<Page<OrdemServicoResponseDTO>> listarOrdens(
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) Boolean filtrarDataCriacao,
        @PageableDefault(sort = "osid") Pageable pageable
    ) {
        Page<OrdemServico> ordens = ordemServicoService.readAll(getUsuarioLogado(), cliente, status, dataInicio, dataFim, filtrarDataCriacao, pageable);
        Page<OrdemServicoResponseDTO> ordensDTOPage = ordens.map(OrdemServicoResponseDTO::new);
        return ResponseEntity.ok(ordensDTOPage);
    }

    @Operation(summary = "Buscar Ordem de Serviço", description = "Retorna uma OS específica pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OS encontrada com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = OrdemServico.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "OS não encontrada ou não pertence ao usuário logado")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<OrdemServicoResponseDTO> buscarOrdem(@PathVariable UUID uuid) {
        return ordemServicoService.readOne(uuid, getUsuarioLogado())
            .map(OrdemServicoResponseDTO::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar Ordem de Serviço", description = "Atualiza os dados de uma OS existente pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OS atualizada com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = OrdemServico.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida (Dados ausentes ou Cliente/Serviço não encontrados)"),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "OS não encontrada ou não pertence ao usuário logado")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarOrdem(@PathVariable UUID uuid, @RequestBody OrdemServicoRequestDTO osrUpdate) {
        return ordemServicoService.update(uuid, osrUpdate, getUsuarioLogado())
            .map(OrdemServicoResponseDTO::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remover Ordem de Serviço", description = "Remove uma OS pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "OS removida com sucesso"),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "OS não encontrada ou não pertence ao usuário logado")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> removerOrdem(@PathVariable UUID uuid) {
        boolean deletado = ordemServicoService.delete(uuid, getUsuarioLogado());
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
