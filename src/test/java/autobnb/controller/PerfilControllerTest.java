package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.*;
import autobnb.model.*;
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

class PerfilControllerTest {

    @Mock
    private ManagerUserSession managerUserSession;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private AlquilerService alquilerService;

    @Mock
    private VehiculoService vehiculoService;

    @InjectMocks
    private PerfilController perfilController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(perfilController).build();
    }

    @Test
    void testPerfil() throws Exception {
        Long usuarioId = 1L;
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(usuarioId);
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.findById(usuarioId)).thenReturn(usuarioData);
        when(usuarioService.buscarUsuarioPorId(anyList(), eq(usuarioId))).thenReturn(usuario);

        mockMvc.perform(get("/perfil/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/perfil"))
                .andExpect(model().attributeExists("logueado"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testCambiarRolUsuario() throws Exception {
        Long usuarioId = 1L;
        String referer = "/perfil/1";

        when(usuarioService.cambiarRolUsuario(eq(usuarioId))).thenReturn(null);

        mockMvc.perform(post("/cambiarRol/{id}", usuarioId)
                        .header("Referer", referer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));
    }

    @Test
    void testMostrarListadoComentarios() throws Exception {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        List<Comentario> comentarios = Collections.singletonList(new Comentario());

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.buscarUsuarioPorId(anyList(), eq(usuarioId))).thenReturn(usuario);
        when(usuarioService.obtenerComentariosPorUsuarioId(usuarioId)).thenReturn(comentarios);

        mockMvc.perform(get("/perfil/{id}/comentarios", usuarioId))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/comentariosUsuario"))
                .andExpect(model().attributeExists("comentarios"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testMostrarListadoAlquileres() throws Exception {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        List<Pago> pagos = Collections.singletonList(new Pago());

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.buscarUsuarioPorId(anyList(), eq(usuarioId))).thenReturn(usuario);
        when(usuarioService.obtenerPagosPorUsuarioId(usuarioId)).thenReturn(pagos);

        mockMvc.perform(get("/perfil/{id}/alquileres", usuarioId))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/alquileresUsuario"))
                .andExpect(model().attributeExists("pagos"))
                .andExpect(model().attributeExists("diasDeAlquiler"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testMostrarListadoAlquileresArrendador() throws Exception {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        List<Vehiculo> vehiculos = Collections.singletonList(new Vehiculo());
        List<Alquiler> alquileres = Collections.singletonList(new Alquiler());

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.buscarUsuarioPorId(anyList(), eq(usuarioId))).thenReturn(usuario);
        when(vehiculoService.obtenerVehiculosPorPropietario(usuarioId)).thenReturn(vehiculos);
        when(alquilerService.obtenerAlquileresPorVehiculos(vehiculos)).thenReturn(alquileres);

        mockMvc.perform(get("/perfil/{id}/alquileres/arrendador", usuarioId))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/alquileresUsuarioArrendador"))
                .andExpect(model().attributeExists("alquileres"))
                .andExpect(model().attributeExists("diasDeAlquiler"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testMostrarVehiculosAlquiladosArrendatario() throws Exception {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        List<Vehiculo> vehiculos = Collections.singletonList(new Vehiculo());
        Page<Vehiculo> vehiculosPage = new PageImpl<>(vehiculos);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.buscarUsuarioPorId(anyList(), eq(usuarioId))).thenReturn(usuario);
        when(vehiculoService.obtenerVehiculosAlquiladosActivosPorUsuario(eq(usuarioId), any(Pageable.class)))
                .thenReturn(vehiculosPage);

        mockMvc.perform(get("/perfil/{id}/vehiculosAlquiladosArrendatario", usuarioId))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/vehiculosAlquiladosArrendatario"))
                .andExpect(model().attributeExists("vehiculosPage"))
                .andExpect(model().attributeExists("cantidad"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testMostrarListadoVehiculos() throws Exception {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        List<Vehiculo> vehiculos = Collections.singletonList(new Vehiculo());
        Page<Vehiculo> vehiculosPage = new PageImpl<>(vehiculos);

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(usuarioService.buscarUsuarioPorId(anyList(), eq(usuarioId))).thenReturn(usuario);
        when(vehiculoService.listadoPaginadoVehiculosDeUsuario(eq(usuarioId), any(Pageable.class)))
                .thenReturn(vehiculosPage);

        mockMvc.perform(get("/perfil/{id}/vehiculos", usuarioId))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil/vehiculosUsuario"))
                .andExpect(model().attributeExists("vehiculosPage"))
                .andExpect(model().attributeExists("cantidad"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    void testEliminarAlquiler() throws Exception {
        Long usuarioId = 1L;
        Long alquilerId = 1L;

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        mockMvc.perform(post("/perfil/{id}/alquileres/eliminar/{alquilerId}", usuarioId, alquilerId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil/" + usuarioId + "/alquileres"));
    }
}
