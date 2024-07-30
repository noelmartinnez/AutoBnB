package autobnb.service;

import autobnb.dto.CategoriaData;
import autobnb.model.Categoria;
import autobnb.model.Vehiculo;
import autobnb.repository.CategoriaRepository;
import autobnb.service.exception.CategoriaServiceException;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Categoria categoria;
    private CategoriaData categoriaData;

    @BeforeEach
    public void setUp() {
        categoria = new Categoria("Deportivo");
        categoria.setId(1L);

        categoriaData = modelMapper.map(categoria, CategoriaData.class);

        Mockito.when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        Mockito.when(categoriaRepository.findByNombre("Deportivo")).thenReturn(Optional.of(categoria));
    }

    @Test
    public void testActualizarCategoria() {
        CategoriaData newData = new CategoriaData();
        newData.setNombre("SUV");

        categoriaService.actualizarCategoria(1L, newData);

        assertThat(categoria.getNombre()).isEqualTo("SUV");
    }

    @Test
    public void testEliminarCategoria() {
        categoriaService.eliminarCategoria(1L);
        Mockito.verify(categoriaRepository, Mockito.times(1)).delete(categoria);
    }

    @Test
    public void testEliminarCategoriaConVehiculosAsociados() {
        Categoria categoriaConVehiculos = new Categoria("Deportivo");
        categoriaConVehiculos.setId(2L);
        categoriaConVehiculos.getVehiculos().add(new Vehiculo());

        Mockito.when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoriaConVehiculos));

        assertThatThrownBy(() -> categoriaService.eliminarCategoria(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se puede eliminar la categoria porque tiene vehículos asociados.");
    }

    @Test
    public void testListarCategoriasPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Categoria> page = new PageImpl<>(Collections.singletonList(categoria));

        Mockito.when(categoriaRepository.findAll(pageable)).thenReturn(page);

        Page<Categoria> result = categoriaService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        CategoriaData result = categoriaService.findById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    public void testFindByNombre() {
        CategoriaData result = categoriaService.findByNombre("Deportivo");
        assertThat(result).isNotNull();
    }

    @Test
    public void testActualizarCategoriaNoEncontrada() {
        assertThatThrownBy(() -> categoriaService.actualizarCategoria(100L, new CategoriaData()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("No se encontró la categoria con ID: 100");
    }

    @Test
    public void testCrearCategoriaExistente() {
        Mockito.when(categoriaRepository.findByNombre(any(String.class))).thenReturn(Optional.of(categoria));
        assertThatThrownBy(() -> categoriaService.crearCategoria(categoriaData))
                .isInstanceOf(CategoriaServiceException.class)
                .hasMessageContaining("ya está registrada");
    }
}
