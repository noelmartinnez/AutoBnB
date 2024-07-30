package autobnb.service;

import autobnb.dto.ComentarioData;
import autobnb.model.Comentario;
import autobnb.model.Marca;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.repository.ComentarioRepository;
import autobnb.repository.UsuarioRepository;
import autobnb.repository.VehiculoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {
    @Autowired
    private ComentarioRepository comentarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public ComentarioData findById(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElse(null);

        if (comentario == null) return null;
        else {
            return modelMapper.map(comentario, ComentarioData.class);
        }
    }

    @Transactional
    public void eliminarComentario(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElse(null);
        if (comentario != null) {
            Usuario usuario = comentario.getUsuario();
            Vehiculo vehiculo = comentario.getVehiculo();

            usuario.getComentarios().remove(comentario);
            vehiculo.getComentarios().remove(comentario);

            usuarioRepository.save(usuario);
            vehiculoRepository.save(vehiculo);

            comentarioRepository.delete(comentario);
        }
    }

    @Transactional
    public void actualizarComentario(Long comentarioId, ComentarioData comentarioData) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElse(null);

        if (comentario != null) {
            comentario.setDescripcion(comentarioData.getDescripcion());
            comentarioRepository.save(comentario);
        } else {
            throw new EntityNotFoundException("No se encontró el comentario con ID: " + comentarioId);
        }
    }

    @Transactional(readOnly = true)
    public Comentario buscarComentarioPorId(List<Comentario> comentarios, Long idBuscado) {
        for (Comentario comentario : comentarios) {
            if (comentario.getId().equals(idBuscado)) {
                return comentario;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Comentario> listadoCompleto() {
        return ((List<Comentario>) comentarioRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Comentario::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Comentario> listadoPaginado(Pageable pageable) {
        return comentarioRepository.findAll(pageable);
    }
}
