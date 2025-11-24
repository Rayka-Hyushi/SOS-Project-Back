package rayka.sos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rayka.sos.model.Cliente;
import rayka.sos.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Buscar todos os clientes do usuário
    Page<Cliente> findByUsuario(Usuario usuario, Pageable pageable);

    // Buscar um cliente específico
    Optional<Cliente> findByUuidAndUsuario(UUID Uuid, Usuario usuario);

    // Retornar clientes cujo nome contenha o filtro, ignorando maiúsculas/minúsculas
    Page<Cliente> findByUsuarioAndNameContainingIgnoreCase(Usuario usuario, String nomeFiltro, Pageable pageable);
}
