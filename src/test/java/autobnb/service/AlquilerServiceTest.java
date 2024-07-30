package autobnb.service;

import autobnb.model.*;
import autobnb.repository.AlquilerRepository;
import autobnb.repository.PagoRepository;
import autobnb.repository.UsuarioRepository;
import autobnb.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class AlquilerServiceTest {

    @Autowired
    private AlquilerService alquilerService;

    @MockBean
    private AlquilerRepository alquilerRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private VehiculoRepository vehiculoRepository;

    @MockBean
    private PagoRepository pagoRepository;

    private Alquiler alquiler;
    private Pago pago;
    private Vehiculo vehiculo;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        Cuenta cuenta = new Cuenta();
        cuenta.setSaldo(new BigDecimal("1000"));
        usuario.setCuenta(cuenta);

        pago = new Pago();
        pago.setId(1L);
        pago.setUsuario(usuario);

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setUsuario(usuario);

        alquiler = new Alquiler(new Date(), new Date(), new Date(), new BigDecimal("200"), vehiculo, pago);
        alquiler.setId(1L);

        Mockito.when(alquilerRepository.findById(1L)).thenReturn(Optional.of(alquiler));
        Mockito.when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        Mockito.when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
    }

    @Test
    public void testEliminarAlquiler() {
        alquilerService.eliminarAlquiler(1L);

        Mockito.verify(alquilerRepository, Mockito.times(1)).delete(alquiler);
    }

    @Test
    public void testCrearAlquiler() {
        Mockito.when(alquilerRepository.save(any(Alquiler.class))).thenReturn(alquiler);

        Alquiler nuevoAlquiler = alquilerService.crearAlquiler(new Date(), new Date(), new Date(), new BigDecimal("200"), null, 1L, 1L);
        assertThat(nuevoAlquiler).isNotNull();
    }

    @Test
    public void testListadoCompleto() {
        List<Alquiler> alquileres = Collections.singletonList(alquiler);
        Mockito.when(alquilerRepository.findAll()).thenReturn(alquileres);

        List<Alquiler> result = alquilerService.listadoCompleto();
        assertThat(result).hasSize(1);
    }

    @Test
    public void testListadoPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Alquiler> page = new PageImpl<>(Collections.singletonList(alquiler));

        Mockito.when(alquilerRepository.findAll(pageable)).thenReturn(page);

        Page<Alquiler> result = alquilerService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testObtenerAlquileresActivosPorVehiculoId() {
        List<Alquiler> alquileres = Collections.singletonList(alquiler);
        Mockito.when(alquilerRepository.findByVehiculoIdAndFechaDevolucionGreaterThanEqual(any(Long.class), any(Date.class)))
                .thenReturn(alquileres);

        List<Alquiler> result = alquilerService.obtenerAlquileresActivosPorVehiculoId(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testObtenerAlquileresPorVehiculos() {
        List<Vehiculo> vehiculos = Collections.singletonList(vehiculo);
        List<Alquiler> alquileres = Collections.singletonList(alquiler);

        Mockito.when(alquilerRepository.findByVehiculoIdIn(any(List.class)))
                .thenReturn(alquileres);

        List<Alquiler> result = alquilerService.obtenerAlquileresPorVehiculos(vehiculos);
        assertThat(result).hasSize(1);
    }
}
