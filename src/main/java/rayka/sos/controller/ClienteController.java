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
import rayka.sos.dto.ClienteResponseDTO;
import rayka.sos.model.Cliente;
import rayka.sos.model.Usuario;
import rayka.sos.service.ClienteService;

import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Path relacionado a operações de clientes")
public class ClienteController {
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Operation(summary = "Cadastrar Cliente", description = "Cria um novo cliente para o usuário logado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado (Token ausente ou inválido)")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> criarCliente(@RequestBody Cliente cliente) {
        Cliente salvo = clienteService.create(cliente, getUsuarioLogado());
        ClienteResponseDTO responseDTO = new ClienteResponseDTO(salvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Listar Clientes", description = "Retorna todos os clientes cadastrados pelo usuário logado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ClienteResponseDTO.class)))),
        @ApiResponse(responseCode = "403", description = "Não autorizado (Token ausente ou inválido)")
    })
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(
        @RequestParam(required = false) String nome,
        @PageableDefault(sort = "name") Pageable pageable
    ) {
        Page<Cliente> clientes = clienteService.readAll(getUsuarioLogado(), nome, pageable);
        Page<ClienteResponseDTO> clientesDTOPage = clientes.map(ClienteResponseDTO::new);
        return ResponseEntity.ok(clientesDTOPage);
    }

    @Operation(summary = "Buscar Cliente", description = "Retorna o perfil de um cliente específico pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado (Token ausente ou inválido)"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou não pertence ao usuário logado")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<ClienteResponseDTO> buscarCliente(@PathVariable UUID uuid) {
        return clienteService.readOne(uuid, getUsuarioLogado())
            .map(ClienteResponseDTO::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar Cliente", description = "Atualiza os dados de um cliente existente pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Não autorizado (Token ausente ou inválido)"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou não pertence ao usuário logado")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(@PathVariable UUID uuid, @RequestBody Cliente clienteUpdate) {
        return clienteService.update(uuid, clienteUpdate, getUsuarioLogado())
            .map(ClienteResponseDTO::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remover Cliente", description = "Remove um cliente pelo UUID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
        @ApiResponse(responseCode = "403", description = "Não autorizado (Token ausente ou inválido)"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou não pertence ao usuário logado")

    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> removerCliente(@PathVariable UUID uuid) {
        boolean deletado = clienteService.delete(uuid, getUsuarioLogado());
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
