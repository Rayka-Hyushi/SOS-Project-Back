package rayka.sos.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rayka.sos.dto.OrdemServicoRequestDTO;
import rayka.sos.model.*;
import rayka.sos.repository.ClienteRepository;
import rayka.sos.repository.OrdemServicoRepository;
import rayka.sos.repository.ServicoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@AllArgsConstructor
public class OrdemServicoService {
    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;

    // Operação de create
    @Transactional
    public OrdemServico create(OrdemServicoRequestDTO osrDTO, Usuario usuario) {
        Cliente cliente = clienteRepository.findByUuidAndUsuario(osrDTO.getClienteUuid(), usuario)
            .orElseThrow(() -> new NoSuchElementException("Cliente não encontrado ou não pertence ao usuário."));

        Set<Servico> servicos = servicoRepository.findByUuidIn(osrDTO.getServicosUuids());

        if (servicos.size() != osrDTO.getServicosUuids().size()) {
            throw new IllegalArgumentException("Um ou mais UUIDs de Serviço são inválidos.");
        }

        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setUsuario(usuario);
        os.setServicos(servicos);
        os.setDevice(osrDTO.getDevice());
        os.setDescription(osrDTO.getDescription());
        os.setStatus(osrDTO.getStatus());
        os.setOpendate(LocalDateTime.now());
        os.setExtras(osrDTO.getExtras());
        os.setDiscount(osrDTO.getDiscount());
        os.setTotal(calculaTotal(osrDTO.getExtras(), osrDTO.getDiscount(), servicos));

        return ordemServicoRepository.save(os);
    }

    // Operação de read de todas as ordens do usuário
    public Page<OrdemServico> readAll(Usuario usuario, String clienteFiltro, String status, LocalDate dataInicio, LocalDate dataFim, Boolean filtrarDataCriacao, Pageable pageable) {
        if (clienteFiltro != null && !clienteFiltro.isEmpty()) {
            return ordemServicoRepository.findByUsuarioAndClienteNameContainingIgnoreCase(
                usuario,
                clienteFiltro,
                pageable
            );
        } else if (status != null && !status.isEmpty()) {
            StatusOrdemServico enumStatus = StatusOrdemServico.valueOf(status);
            return ordemServicoRepository.findByUsuarioAndStatus(
                usuario,
                enumStatus,
                pageable
            );
        } else if (dataInicio != null && dataFim != null) {
            LocalDateTime inicioDia = dataInicio.atStartOfDay();
            LocalDateTime fimDia = dataFim.atTime(LocalTime.MAX);
            if (filtrarDataCriacao) {
                return ordemServicoRepository.findByUsuarioAndOpendateBetween(
                    usuario,
                    inicioDia,
                    fimDia,
                    pageable
                );
            } else {
                return ordemServicoRepository.findByUsuarioAndClosedateBetween(
                    usuario,
                    inicioDia,
                    fimDia,
                    pageable
                );
            }
        } else {
            return ordemServicoRepository.findByUsuario(usuario, pageable);
        }
    }

    // Operação de read único
    public Optional<OrdemServico> readOne(UUID Uuid, Usuario usuario) {
        return ordemServicoRepository.findByUuidAndUsuario(Uuid, usuario);
    }

    // Operacao de update
    @Transactional
    public Optional<OrdemServico> update(UUID osUuid, OrdemServicoRequestDTO osrUpdate, Usuario usuario) {
        Set<Servico> servicos = servicoRepository.findByUuidIn(osrUpdate.getServicosUuids());

        if (servicos.size() != osrUpdate.getServicosUuids().size()) {
            throw new IllegalArgumentException("Um ou mais UUIDs de Serviço são inválidos.");
        }

        return ordemServicoRepository.findByUuidAndUsuario(osUuid, usuario)
            .map(os -> {
                os.setServicos(servicos);
                os.setDevice(osrUpdate.getDevice());
                os.setDescription(osrUpdate.getDescription());
                if (!os.getStatus().equals(osrUpdate.getStatus())) {
                    Integer currentOrder = STATUS_PRIORITY.get(os.getStatus());
                    Integer newOrder = STATUS_PRIORITY.get(osrUpdate.getStatus());

                    if (newOrder == null || currentOrder == null || newOrder < currentOrder) {
                        throw new IllegalArgumentException("A transição de status de " + os.getStatus() + " para " + osrUpdate.getStatus() + " não é permitida. A ordem deve ser crescente.");
                    } else if (osrUpdate.getStatus() == StatusOrdemServico.FINALIZADA) {
                        os.setStatus(osrUpdate.getStatus());
                        os.setClosedate(LocalDateTime.now());
                    } else {
                        os.setStatus(osrUpdate.getStatus());
                    }
                }
                os.setExtras(osrUpdate.getExtras());
                os.setDiscount(osrUpdate.getDiscount());
                os.setTotal(calculaTotal(osrUpdate.getExtras(), osrUpdate.getDiscount(), servicos));
                return ordemServicoRepository.save(os);
            });
    }

    // Operacao de delete
    @Transactional
    public void delete(UUID osUuid, Usuario usuario) {
        OrdemServico os = ordemServicoRepository.findByUuidAndUsuario(osUuid, usuario)
            .orElseThrow(() -> new NoSuchElementException("Ordem de Serviço não encontrada ou não pertence ao usuário."));
        if (os.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new IllegalStateException("Ordem de Serviço finalizada não pode ser deletada.");
        } else {
            if (os.getStatus() != StatusOrdemServico.CONCLUIDA && os.getServicos().isEmpty()) {
                ordemServicoRepository.delete(os);
            } else {
                throw new IllegalStateException("Ordem de Serviço com serviços associados ou em status 'CONCLUIDA' não pode ser deletada.");
            }
        }
    }

    // Função para calcular o valor total da ordem
    private BigDecimal calculaTotal(BigDecimal extras, BigDecimal discount, Set<Servico> servicos) {
        BigDecimal total = BigDecimal.ZERO;
        if (!servicos.isEmpty()) {
            total = servicos.stream()
                .map(Servico::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        total = total
            .add(extras)
            .subtract(discount);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    private static final Map<StatusOrdemServico, Integer> STATUS_PRIORITY = Map.of(
        StatusOrdemServico.ABERTA, 1,
        StatusOrdemServico.EM_ANDAMENTO, 2,
        StatusOrdemServico.CONCLUIDA, 3,
        StatusOrdemServico.FINALIZADA, 4
    );
}
