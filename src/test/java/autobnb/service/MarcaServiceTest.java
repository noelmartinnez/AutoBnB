package autobnb.service;

import autobnb.dto.MarcaData;
import autobnb.model.Marca;
import autobnb.model.Vehiculo;
import autobnb.repository.MarcaRepository;
import autobnb.service.exception.MarcaServiceException;
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
public class MarcaServiceTest {

    @Autowired
    private MarcaService marcaService;

    @MockBean
    private MarcaRepository marcaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Marca marca;
    private MarcaData marcaData;

    @BeforeEach
    public void setUp() {
        marca = new Marca("Toyota");
        marca.setId(1L);

        marcaData = modelMapper.map(marca, MarcaData.class);

        Mockito.when(marcaRepository.findById(1L)).thenReturn(Optional.of(marca));
        Mockito.when(marcaRepository.findByNombre("Toyota")).thenReturn(Optional.of(marca));
    }

    @Test
    public void testActualizarMarca() {
        MarcaData newData = new MarcaData();
        newData.setNombre("Ford");

        marcaService.actualizarMarca(1L, newData);

        assertThat(marca.getNombre()).isEqualTo("Ford");
    }

    @Test
    public void testEliminarMarca() {
        marcaService.eliminarMarca(1L);
        Mockito.verify(marcaRepository, Mockito.times(1)).delete(marca);
    }

    @Test
    public void testEliminarMarcaConVehiculosAsociados() {
        Marca marcaConVehiculos = new Marca("BMW");
        marcaConVehiculos.setId(2L);
        marcaConVehiculos.getVehiculos().add(new Vehiculo());

        Mockito.when(marcaRepository.findById(2L)).thenReturn(Optional.of(marcaConVehiculos));

        assertThatThrownBy(() -> marcaService.eliminarMarca(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede eliminar la marca porque tiene vehículos asociados.");
    }

    @Test
    public void testListarMarcasPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Marca> page = new PageImpl<>(Collections.singletonList(marca));

        Mockito.when(marcaRepository.findAll(pageable)).thenReturn(page);

        Page<Marca> result = marcaService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        MarcaData result = marcaService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testFindByNombre() {
        MarcaData result = marcaService.findByNombre("Toyota");
        assertThat(result).isNotNull();
    }

    @Test
    public void testActualizarMarcaNoEncontrada() {
        assertThatThrownBy(() -> marcaService.actualizarMarca(100L, new MarcaData()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró la marca con ID: 100");
    }

    @Test
    public void testCrearMarcaExistente() {
        Mockito.when(marcaRepository.findByNombre(any(String.class))).thenReturn(Optional.of(marca));
        assertThatThrownBy(() -> marcaService.crearMarca(marcaData))
                .isInstanceOf(MarcaServiceException.class)
                .hasMessageContaining("ya está registrada");
    }
}
