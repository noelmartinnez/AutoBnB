package autobnb.service;

import autobnb.dto.ModeloData;
import autobnb.model.Marca;
import autobnb.model.Modelo;
import autobnb.model.Vehiculo;
import autobnb.repository.MarcaRepository;
import autobnb.repository.ModeloRepository;
import autobnb.service.exception.ModeloServiceException;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ModeloServiceTest {

    @Autowired
    private ModeloService modeloService;

    @MockBean
    private ModeloRepository modeloRepository;

    @MockBean
    private MarcaRepository marcaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Modelo modelo;
    private Marca marca;
    private ModeloData modeloData;

    @BeforeEach
    public void setUp() {
        marca = new Marca("Toyota");
        marca.setId(1L);

        modelo = new Modelo("Corolla", marca);
        modelo.setId(1L);

        modeloData = modelMapper.map(modelo, ModeloData.class);

        Mockito.when(modeloRepository.findById(1L)).thenReturn(Optional.of(modelo));
        Mockito.when(modeloRepository.findByNombre("Corolla")).thenReturn(Optional.of(modelo));
        Mockito.when(marcaRepository.findById(1L)).thenReturn(Optional.of(marca));
    }

    @Test
    public void testActualizarModelo() {
        ModeloData newData = new ModeloData();
        newData.setNombre("Camry");

        modeloService.actualizarModelo(1L, newData);

        assertThat(modelo.getNombre()).isEqualTo("Camry");
    }

    @Test
    public void testEliminarModelo() {
        modeloService.eliminarModelo(1L);
        Mockito.verify(modeloRepository, Mockito.times(1)).delete(modelo);
    }

    @Test
    public void testEliminarModeloConVehiculosAsociados() {
        Modelo modeloConVehiculos = new Modelo("Camry", marca);
        modeloConVehiculos.setId(2L);
        modeloConVehiculos.getVehiculos().add(new Vehiculo());

        Mockito.when(modeloRepository.findById(2L)).thenReturn(Optional.of(modeloConVehiculos));

        assertThatThrownBy(() -> modeloService.eliminarModelo(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede eliminar el modelo porque tiene vehículos asociados.");
    }

    @Test
    public void testListarModelosPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Modelo> page = new PageImpl<>(Collections.singletonList(modelo));

        Mockito.when(modeloRepository.findAll(pageable)).thenReturn(page);

        Page<Modelo> result = modeloService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        ModeloData result = modeloService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testFindByNombre() {
        ModeloData result = modeloService.findByNombre("Corolla");
        assertThat(result).isNotNull();
    }

    @Test
    public void testActualizarModeloNoEncontrado() {
        assertThatThrownBy(() -> modeloService.actualizarModelo(100L, new ModeloData()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró el modelo con ID: 100");
    }

    @Test
    public void testCrearModeloExistente() {
        Mockito.when(modeloRepository.findByNombre(any(String.class))).thenReturn(Optional.of(modelo));
        assertThatThrownBy(() -> modeloService.crearModelo(modeloData))
                .isInstanceOf(ModeloServiceException.class)
                .hasMessageContaining("ya está registrada");
    }
}
