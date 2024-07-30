package autobnb.service;

import autobnb.dto.TransmisionData;
import autobnb.model.Transmision;
import autobnb.model.Vehiculo;
import autobnb.repository.TransmisionRepository;
import autobnb.service.exception.TransmisionServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
public class TransmisionServiceTest {

    @Autowired
    private TransmisionService transmisionService;

    @MockBean
    private TransmisionRepository transmisionRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Transmision transmision;
    private TransmisionData transmisionData;

    @BeforeEach
    public void setUp() {
        transmision = new Transmision("Automático");
        transmision.setId(1L);

        transmisionData = modelMapper.map(transmision, TransmisionData.class);

        Mockito.when(transmisionRepository.findById(1L)).thenReturn(Optional.of(transmision));
        Mockito.when(transmisionRepository.findByNombre("Automático")).thenReturn(Optional.of(transmision));
    }

    @Test
    public void testActualizarTransmision() {
        TransmisionData newData = new TransmisionData();
        newData.setNombre("Manual");

        transmisionService.actualizarTransmision(1L, newData);

        assertThat(transmision.getNombre()).isEqualTo("Manual");
    }

    @Test
    public void testEliminarTransmision() {
        transmisionService.eliminarTransmision(1L);
        Mockito.verify(transmisionRepository, Mockito.times(1)).delete(transmision);
    }

    @Test
    public void testEliminarTransmisionConVehiculosAsociados() {
        Transmision transmisionConVehiculos = new Transmision("CVT");
        transmisionConVehiculos.setId(2L);
        transmisionConVehiculos.getVehiculos().add(new Vehiculo());

        Mockito.when(transmisionRepository.findById(2L)).thenReturn(Optional.of(transmisionConVehiculos));

        assertThatThrownBy(() -> transmisionService.eliminarTransmision(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede eliminar la transmisión porque tiene vehículos asociados.");
    }

    @Test
    public void testListarTransmisionesPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Transmision> page = new PageImpl<>(Collections.singletonList(transmision));

        Mockito.when(transmisionRepository.findAll(pageable)).thenReturn(page);

        Page<Transmision> result = transmisionService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        TransmisionData result = transmisionService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testFindByNombre() {
        TransmisionData result = transmisionService.findByNombre("Automático");
        assertThat(result).isNotNull();
    }

    @Test
    public void testActualizarTransmisionNoEncontrada() {
        assertThatThrownBy(() -> transmisionService.actualizarTransmision(100L, new TransmisionData()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró la transmisión con ID: 100");
    }

    @Test
    public void testCrearTransmisionExistente() {
        Mockito.when(transmisionRepository.findByNombre(any(String.class))).thenReturn(Optional.of(transmision));
        assertThatThrownBy(() -> transmisionService.crearTransmision(transmisionData))
                .isInstanceOf(TransmisionServiceException.class)
                .hasMessageContaining("ya está registrada");
    }
}
