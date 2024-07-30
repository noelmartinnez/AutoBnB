package autobnb.controller;

import autobnb.dto.ComentarioData;
import autobnb.service.ComentarioService;
import autobnb.service.UsuarioService;
import autobnb.authentication.ManagerUserSession;
import autobnb.dto.UsuarioData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AdministracionControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ComentarioService comentarioService;

    @Mock
    private ManagerUserSession managerUserSession;

    @InjectMocks
    private AdministracionController administracionController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(administracionController).build();
    }

    @Test
    public void testPanelAdministracion() throws Exception {
        Long userId = 1L;
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(userId);
        usuarioData.setAdministrador(true);

        when(managerUserSession.usuarioLogeado()).thenReturn(userId);
        when(usuarioService.findById(userId)).thenReturn(usuarioData);

        mockMvc.perform(get("/administracion"))
                .andExpect(status().isOk())
                .andExpect(view().name("administracion/panelAdministrador"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attribute("usuario", usuarioData));
    }
}
