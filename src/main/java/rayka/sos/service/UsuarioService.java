package rayka.sos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rayka.sos.dto.UsuarioRequestDTO;
import rayka.sos.model.Usuario;
import rayka.sos.repository.UsuarioRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Operação de create
    public String create(UsuarioRequestDTO usuarioRequestDTO, MultipartFile photo) {
        Usuario usuario = new Usuario();

        // Criptografia da senha e adição dos campos
        usuario.setPass(new BCryptPasswordEncoder().encode(usuarioRequestDTO.getPass()));
        usuario.setName(usuarioRequestDTO.getName());
        usuario.setEmail(usuarioRequestDTO.getEmail());

        // Conversão da foto para bytes
        if (photo != null && !photo.isEmpty()) {
            try {
                usuario.setPhoto(photo.getBytes());
                System.out.println(Arrays.toString(usuario.getPhoto()));
                usuario.setPhotoType(photo.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a imagem enviada. " + e);
            }
        }

        // Salva o novo usuário
        Usuario salvo = usuarioRepository.save(usuario);
        if (salvo.getName() == null) {
            return "Erro ao salvar o usuário.";
        }
        return "Usuário criado com sucesso.";
    }

    public Optional<Usuario> update(UUID uuid, UsuarioRequestDTO usuarioUpdate) {
        return usuarioRepository.findByUuid(uuid).map(usuario -> {
            usuario.setName(usuarioUpdate.getName());
            usuario.setEmail(usuarioUpdate.getEmail());

            if (usuarioUpdate.getPass() != null && !usuarioUpdate.getPass().isEmpty()) {
                String passwordCoded = new BCryptPasswordEncoder().encode(usuarioUpdate.getPass());
                usuario.setPass(passwordCoded);
            }

            return usuarioRepository.save(usuario);
        });
    }

    public Usuario updatePhoto(UUID uuid, MultipartFile photo) {
        Usuario usuario = usuarioRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (photo != null && !photo.isEmpty()) {
            try {
                usuario.setPhoto(photo.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a imagem enviada. " + e);
            }
        }

        return usuarioRepository.save(usuario);
    }

    public void delete(UUID uuid) {
        usuarioRepository.findByUuid(uuid).ifPresent(usuarioRepository::delete);
    }
}
