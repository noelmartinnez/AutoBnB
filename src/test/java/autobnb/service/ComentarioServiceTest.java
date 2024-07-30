package autobnb.service;

import autobnb.dto.ComentarioData;
import autobnb.model.Comentario;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.repository.ComentarioRepository;
import autobnb.repository.UsuarioRepository;
import autobnb.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ComentarioServiceTest {

    @Autowired
    private ComentarioService comentarioService;

    @MockBean
    private ComentarioRepository comentarioRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Comentario comentario;
    private Usuario usuario;
    private Vehiculo vehiculo;
    private ComentarioData comentarioData;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);

        comentario = new Comentario("Buen vehículo", new java.util.Date(), vehiculo, usuario);
        comentario.setId(1L);

        comentarioData = modelMapper.map(comentario, ComentarioData.class);

        Mockito.when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentario));
    }

    @Test
    public void testEliminarComentario() {
        comentarioService.eliminarComentario(1L);
        Mockito.verify(comentarioRepository, Mockito.times(1)).delete(comentario);
    }

    @Test
    public void testActualizarComentario() {
        ComentarioData newData = new ComentarioData();
        newData.setDescripcion("Excelente vehículo");

        comentarioService.actualizarComentario(1L, newData);

        assertThat(comentario.getDescripcion()).isEqualTo("Excelente vehículo");
    }

    @Test
    public void testListarComentarios() {
        List<Comentario> comentarios = Collections.singletonList(comentario);
        Mockito.when(comentarioRepository.findAll()).thenReturn(comentarios);

        List<Comentario> result = comentarioService.listadoCompleto();
        assertThat(result).hasSize(1);
    }

    @Test
    public void testListarComentariosPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comentario> page = new PageImpl<>(Collections.singletonList(comentario));

        Mockito.when(comentarioRepository.findAll(pageable)).thenReturn(page);

        Page<Comentario> result = comentarioService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        ComentarioData result = comentarioService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testActualizarComentarioNoEncontrado() {
        assertThatThrownBy(() -> comentarioService.actualizarComentario(100L, new ComentarioData()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró el comentario con ID: 100");
    }
}
