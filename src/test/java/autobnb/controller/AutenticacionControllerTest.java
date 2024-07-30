package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.dto.*;
import autobnb.service.CuentaService;
import autobnb.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AutenticacionControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private ManagerUserSession managerUserSession;

    @InjectMocks
    private AutenticacionController autenticacionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(autenticacionController).build();
    }

    @Test
    void testLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("loginData"));
    }

    @Test
    void testLoginSubmitOk() throws Exception {
        String email = "test@example.com";
        String password = "password";
        UsuarioData usuario = new UsuarioData();
        usuario.setId(1L);

        when(usuarioService.login(eq(email), eq(password))).thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail(eq(email))).thenReturn(usuario);

        mockMvc.perform(post("/login")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void testLoginSubmitUserNotFound() throws Exception {
        String email = "test@example.com";
        String password = "password";

        when(usuarioService.login(eq(email), eq(password))).thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        mockMvc.perform(post("/login")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testLoginSubmitErrorPassword() throws Exception {
        String email = "test@example.com";
        String password = "wrongpassword";

        when(usuarioService.login(eq(email), eq(password))).thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        mockMvc.perform(post("/login")
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void testRegistroForm() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("formRegistro"))
                .andExpect(model().attributeExists("registroData"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
