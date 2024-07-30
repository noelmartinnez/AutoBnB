package autobnb.service;

import autobnb.dto.VehiculoData;
import autobnb.model.*;
import autobnb.repository.*;
import autobnb.service.exception.VehiculoServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VehiculoServiceTest {

    @Autowired
    private VehiculoService vehiculoService;

    @MockBean
    private VehiculoRepository vehiculoRepository;

    @MockBean
    private MarcaRepository marcaRepository;

    @MockBean
    private ModeloRepository modeloRepository;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @MockBean
    private AlquilerRepository alquilerRepository;

    @MockBean
    private ColorRepository colorRepository;

    @MockBean
    private TransmisionRepository transmisionRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ComentarioRepository comentarioRepository;

    private Vehiculo vehiculo;
    private Marca marca;
    private Modelo modelo;
    private Categoria categoria;
    private Color color;
    private Transmision transmision;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        marca = new Marca();
        marca.setId(1L);
        marca.setNombre("Toyota");

        modelo = new Modelo();
        modelo.setId(1L);
        modelo.setNombre("Corolla");
        modelo.setMarca(marca);

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Sedán");

        color = new Color();
        color.setId(1L);
        color.setNombre("Rojo");

        transmision = new Transmision();
        transmision.setId(1L);
        transmision.setNombre("Automático");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@test.com");

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setCategoria(categoria);
        vehiculo.setColor(color);
        vehiculo.setTransmision(transmision);
        vehiculo.setUsuario(usuario);
    }

    @Test
    public void testListadoCompleto() {
        List<Vehiculo> vehiculos = Collections.singletonList(vehiculo);
        when(vehiculoRepository.findAll()).thenReturn(vehiculos);

        List<Vehiculo> result = vehiculoService.listadoCompleto();
        assertThat(result).containsExactlyElementsOf(vehiculos);
    }

    @Test
    public void testActualizarVehiculo() {
        VehiculoData vehiculoData = new VehiculoData();
        vehiculoData.setMatricula("ABC1234");

        when(vehiculoRepository.findById(anyLong())).thenReturn(Optional.of(vehiculo));

        vehiculoService.actualizarVehiculo(vehiculo.getId(), vehiculoData);

        verify(vehiculoRepository).save(any(Vehiculo.class));
    }

    @Test
    public void testEliminarVehiculo() {
        when(vehiculoRepository.findById(anyLong())).thenReturn(Optional.of(vehiculo));

        vehiculoService.eliminarVehiculo(vehiculo.getId());

        verify(vehiculoRepository).delete(any(Vehiculo.class));
    }

    @Test
    public void testFindById() {
        when(vehiculoRepository.findById(anyLong())).thenReturn(Optional.of(vehiculo));

        VehiculoData result = vehiculoService.findById(vehiculo.getId());
        assertThat(result).isNotNull();
    }

    @Test
    public void testBuscarVehiculoPorId() {
        List<Vehiculo> vehiculos = Collections.singletonList(vehiculo);

        Vehiculo result = vehiculoService.buscarVehiculoPorId(vehiculos, vehiculo.getId());
        assertThat(result).isEqualTo(vehiculo);
    }

    @Test
    public void testObtenerComentariosPorVehiculoId() {
        List<Comentario> comentarios = Arrays.asList(new Comentario(), new Comentario());
        when(comentarioRepository.findByVehiculoId(anyLong())).thenReturn(comentarios);

        List<Comentario> result = vehiculoService.obtenerComentariosPorVehiculoId(vehiculo.getId());
        assertThat(result).containsExactlyElementsOf(comentarios);
    }

    @Test
    public void testObtenerAlquileresPorVehiculoId() {
        List<Alquiler> alquileres = Arrays.asList(new Alquiler(), new Alquiler());
        when(alquilerRepository.findByVehiculoId(anyLong())).thenReturn(alquileres);

        List<Alquiler> result = vehiculoService.obtenerAlquileresPorVehiculoId(vehiculo.getId());
        assertThat(result).containsExactlyElementsOf(alquileres);
    }

    @Test
    public void testObtenerUsuarioPorVehiculoId() {
        when(vehiculoRepository.findById(anyLong())).thenReturn(Optional.of(vehiculo));

        Usuario result = vehiculoService.obtenerUsuarioPorVehiculoId(vehiculo.getId());
        assertThat(result).isEqualTo(usuario);
    }

    @Test
    public void testListadoPaginadoVehiculosDeUsuario() {
        Page<Vehiculo> vehiculos = mock(Page.class);
        when(vehiculoRepository.findByUsuarioId(anyLong(), any(Pageable.class))).thenReturn(vehiculos);

        Page<Vehiculo> result = vehiculoService.listadoPaginadoVehiculosDeUsuario(usuario.getId(), Pageable.unpaged());
        assertThat(result).isEqualTo(vehiculos);
    }

    // Agrega más tests para otros métodos específicos si es necesario...
}
