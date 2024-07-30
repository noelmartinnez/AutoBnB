package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.UsuarioData;
import autobnb.dto.VehiculoData;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VehiculoControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private VehiculoService vehiculoService;

    @Mock
    private ManagerUserSession managerUserSession;

    @InjectMocks
    private VehiculoController vehiculoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vehiculoController).build();
    }

    @Test
    void testAgregarComentarioListado() throws Exception {
        Long vehiculoId = 1L;
        Long usuarioId = 1L;
        String descripcion = "Comentario de prueba";

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        mockMvc.perform(post("/comentarios/agregar/{vehiculoId}", vehiculoId)
                        .param("descripcion", descripcion))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listado-vehiculos/detalles-vehiculo/" + vehiculoId));

        verify(vehiculoService).agregarComentarioAVehiculo(vehiculoId, usuarioId, descripcion);
    }

    @Test
    void testAgregarComentarioOfertas() throws Exception {
        Long vehiculoId = 1L;
        Long usuarioId = 1L;
        String descripcion = "Comentario de prueba";

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        mockMvc.perform(post("/comentarios/ofertas/agregar/{vehiculoId}", vehiculoId)
                        .param("descripcion", descripcion))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listado-vehiculos/detalles-vehiculo/oferta/" + vehiculoId));

        verify(vehiculoService).agregarComentarioAVehiculo(vehiculoId, usuarioId, descripcion);
    }

    @Test
    void testAgregarComentarioHome() throws Exception {
        Long vehiculoId = 1L;
        Long usuarioId = 1L;
        String descripcion = "Comentario de prueba";

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        mockMvc.perform(post("/comentarios/home/agregar/{vehiculoId}", vehiculoId)
                        .param("descripcion", descripcion))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home/detalles-vehiculo/" + vehiculoId));

        verify(vehiculoService).agregarComentarioAVehiculo(vehiculoId, usuarioId, descripcion);
    }
}
