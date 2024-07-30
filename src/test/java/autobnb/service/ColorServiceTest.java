package autobnb.service;

import autobnb.dto.ColorData;
import autobnb.model.Color;
import autobnb.model.Vehiculo;
import autobnb.repository.ColorRepository;
import autobnb.service.exception.ColorServiceException;
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
public class ColorServiceTest {

    @Autowired
    private ColorService colorService;

    @MockBean
    private ColorRepository colorRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Color color;
    private ColorData colorData;

    @BeforeEach
    public void setUp() {
        color = new Color("Rojo");
        color.setId(1L);

        colorData = modelMapper.map(color, ColorData.class);

        Mockito.when(colorRepository.findById(1L)).thenReturn(Optional.of(color));
        Mockito.when(colorRepository.findByNombre("Rojo")).thenReturn(Optional.of(color));
    }

    @Test
    public void testActualizarColor() {
        ColorData newData = new ColorData();
        newData.setNombre("Azul");

        colorService.actualizarColor(1L, newData);

        assertThat(color.getNombre()).isEqualTo("Azul");
    }

    @Test
    public void testEliminarColor() {
        colorService.eliminarColor(1L);
        Mockito.verify(colorRepository, Mockito.times(1)).delete(color);
    }

    @Test
    public void testEliminarColorConVehiculosAsociados() {
        Color colorConVehiculos = new Color("Verde");
        colorConVehiculos.setId(2L);
        colorConVehiculos.getVehiculos().add(new Vehiculo());

        Mockito.when(colorRepository.findById(2L)).thenReturn(Optional.of(colorConVehiculos));

        assertThatThrownBy(() -> colorService.eliminarColor(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede eliminar el color porque tiene vehículos asociados.");
    }

    @Test
    public void testListarColoresPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Color> page = new PageImpl<>(Collections.singletonList(color));

        Mockito.when(colorRepository.findAll(pageable)).thenReturn(page);

        Page<Color> result = colorService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        ColorData result = colorService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testFindByNombre() {
        ColorData result = colorService.findByNombre("Rojo");
        assertThat(result).isNotNull();
    }

    @Test
    public void testActualizarColorNoEncontrado() {
        assertThatThrownBy(() -> colorService.actualizarColor(100L, new ColorData()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró el color con ID: 100");
    }

    @Test
    public void testCrearColorExistente() {
        Mockito.when(colorRepository.findByNombre(any(String.class))).thenReturn(Optional.of(color));
        assertThatThrownBy(() -> colorService.crearColor(colorData))
                .isInstanceOf(ColorServiceException.class)
                .hasMessageContaining("ya está registrado");
    }
}
