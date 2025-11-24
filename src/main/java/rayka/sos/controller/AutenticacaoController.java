package rayka.sos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rayka.sos.model.Usuario;
import rayka.sos.security.TokenServiceJWT;

@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para Login e geração de Token JWT")
public class AutenticacaoController {
    private final AuthenticationManager manager;
    private final TokenServiceJWT tokenService;

    @Operation(summary = "Login do Usuário", description = "Autentica o usuário com e-mail e senha e retorna um Token JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso. Token JWT retornado.",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DadosTokenJWT.class))), // Sucesso
        @ApiResponse(responseCode = "400", description = "Credenciais inválidas (E-mail ou senha incorretos)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "Usuário não encontrado: [email]"))) // Falha
    })
    @PostMapping
    public ResponseEntity login(@RequestBody DadosAutenticacao dados) {
        try {
            UsernamePasswordAuthenticationToken autenticado = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
            Authentication at = manager.authenticate(autenticado);

            Usuario usuario = (Usuario) at.getPrincipal();
            String token = tokenService.gerarToken(usuario);

            return ResponseEntity.ok().body(new DadosTokenJWT(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Schema(description = "Estrutura de resposta para o Token JWT")
    private record DadosTokenJWT(@Schema(description = "Token de autenticação JWT",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String token) {
    }

    @Schema(description = "Estrutura de requisição para as credenciais de login")
    private record DadosAutenticacao(
        @Schema(description = "E-mail do usuário", example = "admin@rayka.sos") String login,
        @Schema(description = "Senha do usuário", example = "123456") String senha) {
    }
}
