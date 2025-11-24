package rayka.sos.utils;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarErroDadosInvalidos(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getFieldErrors();
        List<DadosErroValidacao> dados = new ArrayList<>();
        for (FieldError fe : errors) {
            dados.add(new DadosErroValidacao(fe.getField(), fe.getDefaultMessage()));
        }
        return ResponseEntity.badRequest().body(dados);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity tratarErroViolacaoIntegridade(DataIntegrityViolationException ex) {
        System.err.println("Erro de Integridade de Dados no Banco: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Erro de integridade de dados: E-mail já cadastrado ou muito grande.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity tratarErro403(RuntimeException ex) {
        System.out.println("Erro de autenticação: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity tratarErro500(Exception ex) {
        System.out.println("Erro interno no servidor: Tipo: " + ex.getClass().getName());
        System.out.println("Erro interno no servidor: Mensagem: " + ex.getMessage());

        if (ex.getCause() != null) {
            System.out.println("Causa raiz: Tipo: " + ex.getCause().getClass().getName());
            System.out.println("Causa raiz: Mensagem: " + ex.getCause().getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private record DadosErroValidacao(String campo, String mensagem) {
    }
}
