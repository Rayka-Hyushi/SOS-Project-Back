package rayka.sos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rayka.sos.model.OrdemServico;
import rayka.sos.model.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    // Buscar todas as ordens de serviço do usuário
    Page<OrdemServico> findByUsuario(Usuario usuario, Pageable pageable);

    // Buscar uma ordem de serviço específica
    Optional<OrdemServico> findByUuidAndUsuario(UUID Uuid, Usuario usuario);

    // 1. Busca por Cliente
    Page<OrdemServico> findByUsuarioAndClienteContainingIgnoreCase(Usuario usuario, String nomeCliente, Pageable pageable);

    // 2. Busca por Status
    Page<OrdemServico> findByUsuarioAndStatus(Usuario usuario, String status, Pageable pageable);

    // 3. Busca por Data de Criação (intervalo)
    Page<OrdemServico> findByUsuarioAndOpendateBetween(Usuario usuario, LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    // 4. Busca por Data de Encerramento (intervalo)
    Page<OrdemServico> findByUsuarioAndClosedateBetween(Usuario usuario, LocalDate dataInicio, LocalDate dataFim, Pageable pageable);
}
