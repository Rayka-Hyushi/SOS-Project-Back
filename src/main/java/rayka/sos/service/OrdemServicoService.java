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
            return ordemServicoRepository.findByUsuarioAndClienteContainingIgnoreCase(
                usuario,
                clienteFiltro,
                pageable
            );
        } else if (status != null && !status.isEmpty()) {
            return ordemServicoRepository.findByUsuarioAndStatus(
                usuario,
                status,
                pageable
            );
        } else if (dataInicio != null && dataFim != null) {
            if (filtrarDataCriacao) {
                return ordemServicoRepository.findByUsuarioAndDataCriacaoBetween(
                    usuario,
                    dataInicio,
                    dataFim,
                    pageable
                );
            }
            else {
                return ordemServicoRepository.findByUsuarioAndDataEncerramentoBetween(
                    usuario,
                    dataInicio,
                    dataFim,
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
                if (osrUpdate.getStatus().equals(StatusOrdemServico.FINALIZADA)) {
                    os.setStatus(StatusOrdemServico.FINALIZADA);
                    os.setClosedate(LocalDateTime.now());
                } else {
                    os.setStatus(osrUpdate.getStatus());
                }
                os.setExtras(osrUpdate.getExtras());
                os.setDiscount(osrUpdate.getDiscount());
                os.setTotal(calculaTotal(osrUpdate.getExtras(), osrUpdate.getDiscount(), servicos));
                return ordemServicoRepository.save(os);
            });
    }

    // Operacao de delete
    @Transactional
    public boolean delete(UUID osUuid, Usuario usuario) {
        Optional<OrdemServico> os = ordemServicoRepository.findByUuidAndUsuario(osUuid, usuario);
        if (os.isPresent()) {
            ordemServicoRepository.delete(os.get());
            return true;
        }
        return false;
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
}
