package rayka.sos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rayka.sos.model.Servico;
import rayka.sos.model.Usuario;

import java.util.*;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    // Buscar todos os serviços do usuário
    Page<Servico> findByUsuario(Usuario usuario, Pageable pageable);

    // Buscar um serviço específico
    Optional<Servico> findByUuidAndUsuario(UUID Uuid, Usuario usuario);

    // Buscar todos os serviços cujos UUIDs estão na coleção fornecida
    Set<Servico> findByUuidIn(Collection<UUID> uuids);

    // Retornar serviços cujo nome contenha o filtro, ignorando maiúsculas/minúsculas
    Page<Servico> findByUsuarioAndServiceContainingIgnoreCase(Usuario usuario, String nomeFiltro, Pageable pageable);
}
