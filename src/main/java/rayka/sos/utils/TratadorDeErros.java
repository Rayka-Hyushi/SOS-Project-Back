package rayka.sos.utils;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroDadosInvalidos(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.toList());
        ErroResponse body = new ErroResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(),
            "Dados inválidos", "Requisição contém campos inválidos", detalhes);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarErroViolacaoIntegridade(DataIntegrityViolationException ex) {
        System.err.println("Erro de Integridade de Dados no Banco: " + ex.getMessage());
        List<String> detalhes = List.of("Erro de integridade de dados: E-mail já cadastrado ou valor muito grande.");
        ErroResponse body = new ErroResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(),
            "Violação de integridade", "Dados violam restrições do banco", detalhes);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponse> tratarErro403(RuntimeException ex) {
        System.out.println("Erro de autenticação: " + ex.getMessage());
        List<String> detalhes = List.of(ex.getMessage() == null ? "Acesso negado" : ex.getMessage());
        ErroResponse body = new ErroResponse(Instant.now(), HttpStatus.FORBIDDEN.value(),
            "Acesso negado", "Erro de autenticação/autorizaçao", detalhes);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErroResponse> tratarErro404(NoSuchElementException ex) {
        List<String> detalhes = List.of(ex.getMessage() == null ? "Recurso não encontrado" : ex.getMessage());
        ErroResponse body = new ErroResponse(Instant.now(), HttpStatus.NOT_FOUND.value(),
            "Não encontrado", "Recurso solicitado não existe", detalhes);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErro500(Exception ex) {
        System.out.println("Erro interno no servidor: Tipo: " + ex.getClass().getName());
        System.out.println("Erro interno no servidor: Mensagem: " + ex.getMessage());
        if (ex.getCause() != null) {
            System.out.println("Causa raiz: Tipo: " + ex.getCause().getClass().getName());
            System.out.println("Causa raiz: Mensagem: " + ex.getCause().getMessage());
        }

        List<String> detalhes = new ArrayList<>();
        detalhes.add(ex.getMessage() == null ? "Erro interno" : ex.getMessage());
        if (ex.getCause() != null) {
            detalhes.add("Causa: " + ex.getCause().getMessage());
        }

        ErroResponse body = new ErroResponse(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno", "Ocorreu um erro no servidor", detalhes);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
