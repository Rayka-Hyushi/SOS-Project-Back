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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rayka.sos.dto.ServicoResponseDTO;
import rayka.sos.model.Servico;
import rayka.sos.model.Usuario;
import rayka.sos.service.ServicoService;

import java.util.UUID;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Path relacionado a operações de serviços")
public class ServicoController {
    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Operation(summary = "Criar Serviço", description = "Cria um novo serviço associado ao usuário logado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ServicoResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "400", description = "Dados do serviço inválidos")
    })
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criarServico(@RequestBody Servico servico) {
        Servico salvo = servicoService.create(servico, getUsuarioLogado());
        ServicoResponseDTO responseDTO = new ServicoResponseDTO(salvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Listar Serviços", description = "Retorna todos os serviços cadastrados pelo usuário logado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ServicoResponseDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Não autorizado")
    })
    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> listarServicos(
        @RequestParam(required = false) String nome,
        @PageableDefault(sort = "service") Pageable pageable
    ) {
        Page<Servico> servicos = servicoService.readAll(getUsuarioLogado(), nome, pageable);
        Page<ServicoResponseDTO> servicosDTOPage = servicos.map(ServicoResponseDTO::new);
        return ResponseEntity.ok(servicosDTOPage);
    }

    @Operation(summary = "Buscar Serviço", description = "Retorna o perfil de um serviço específico pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço encontrado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ServicoResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado ou não pertence ao usuário logado")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<ServicoResponseDTO> buscarServico(@PathVariable UUID uuid) {
        return servicoService.readOne(uuid, getUsuarioLogado())
            .map(ServicoResponseDTO::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar Serviço", description = "Atualiza os dados de um serviço existente pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ServicoResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado ou não pertence ao usuário logado")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<ServicoResponseDTO> atualizarServico(@PathVariable UUID uuid, @RequestBody Servico servico) {
        return servicoService.update(uuid, servico, getUsuarioLogado())
            .map(ServicoResponseDTO::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remover Serviço", description = "Remove um serviço pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Serviço removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado ou não pertence ao usuário logado")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> removerServico(@PathVariable UUID uuid) {
        boolean deletado = servicoService.delete(uuid, getUsuarioLogado());
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
