package rayka.sos.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rayka.sos.model.Cliente;
import rayka.sos.model.Usuario;
import rayka.sos.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    // Operacao de create
    @Transactional
    public Cliente create(Cliente cliente, Usuario usuario) {
        cliente.setUsuario(usuario);
        return clienteRepository.save(cliente);
    }

    // Operacao de read de todos os clientes do usuario
    public Page<Cliente> readAll(Usuario usuario, String nomeFiltro, Pageable pageable) {
        if (nomeFiltro != null && !nomeFiltro.isEmpty()) {
            return clienteRepository.findByUsuarioAndNameContainingIgnoreCase(usuario, nomeFiltro, pageable);
        }
        return clienteRepository.findByUsuario(usuario, pageable);
    }

    // Operacao de read unico
    public Optional<Cliente> readOne(UUID uuid, Usuario usuario) {
        return clienteRepository.findByUuidAndUsuario(uuid, usuario);
    }

    // Operacao de update
    @Transactional
    public Optional<Cliente> update(UUID clienteUuid, Cliente clienteUpdate, Usuario usuario) {
        return clienteRepository.findByUuidAndUsuario(clienteUuid, usuario)
            .map(cliente -> {
                cliente.setName(clienteUpdate.getName());
                cliente.setPhone(clienteUpdate.getPhone());
                cliente.setEmail(clienteUpdate.getEmail());
                cliente.setAddress(clienteUpdate.getAddress());
                return clienteRepository.save(cliente);
            });
    }

    // Operacao de delete
    @Transactional
    public boolean delete(UUID clienteUuid, Usuario usuario) {
        Optional<Cliente> cliente = clienteRepository.findByUuidAndUsuario(clienteUuid, usuario);
        if (cliente.isPresent()) {
            clienteRepository.delete(cliente.get());
            return true;
        }
        return false;
    }
}
