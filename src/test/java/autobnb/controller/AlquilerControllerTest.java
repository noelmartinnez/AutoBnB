package autobnb.controller;

import autobnb.model.*;
import autobnb.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlquilerControllerTest {

    @Mock
    private VehiculoService vehiculoService;

    @InjectMocks
    private AlquilerController alquilerController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(alquilerController).build();
    }

    @Test
    void testGetAlquileres() throws Exception {
        Long vehiculoId = 1L;
        List<Alquiler> alquileres = new ArrayList<>();
        Alquiler alquiler = new Alquiler();
        alquiler.setFechaEntrega(new Date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        alquiler.setFechaDevolucion(cal.getTime());
        alquileres.add(alquiler);

        when(vehiculoService.obtenerAlquileresPorVehiculoId(vehiculoId)).thenReturn(alquileres);

        mockMvc.perform(get("/api/alquileres/{vehiculoId}", vehiculoId))
                .andExpect(status().isOk());
    }
}
