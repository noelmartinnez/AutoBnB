package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.BusquedaHomeData;
import autobnb.dto.UsuarioData;
import autobnb.dto.VehiculoData;
import autobnb.model.Marca;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import autobnb.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehiculoService vehiculoService;

    @MockBean
    private MarcaService marcaService;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TransmisionService transmisionService;

    @MockBean
    private ColorService colorService;

    @MockBean
    private ManagerUserSession managerUserSession;

    private Usuario usuario;
    private Vehiculo vehiculo;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Test");

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPrecioPorDia(new BigDecimal("100"));
        vehiculo.setOferta(10);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());
        when(usuarioService.findById(usuario.getId())).thenReturn(new UsuarioData());
        when(usuarioService.buscarUsuarioPorId(Mockito.anyList(), Mockito.eq(usuario.getId()))).thenReturn(usuario);

        when(vehiculoService.listadoVehiculosConOferta()).thenReturn(Collections.singletonList(vehiculo));
        when(marcaService.listadoCompleto()).thenReturn(Collections.singletonList(new Marca()));
    }

    @Test
    public void testInit() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
