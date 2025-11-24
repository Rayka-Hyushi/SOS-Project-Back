package rayka.sos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "API Service Order System",
        version = "1.0",
        description = "Documentação da API Service Order System",
        contact = @Contact(name = "Suporte", email = "contato.rayka@outlook.com")
    )
)
@SpringBootApplication
public class ServiceOrderSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderSystemApplication.class, args);
    }

}
