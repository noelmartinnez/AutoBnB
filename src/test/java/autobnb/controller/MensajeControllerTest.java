package autobnb.controller;

import autobnb.authentication.ManagerUserSession;
import autobnb.model.Usuario;
import autobnb.service.MensajeService;
import autobnb.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MensajeService mensajeService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    private Usuario remitente;
    private Usuario destinatario;

    @BeforeEach
    public void setUp() {
        remitente = new Usuario();
        remitente.setId(1L);
        remitente.setNombre("Remitente");

        destinatario = new Usuario();
        destinatario.setId(2L);
        destinatario.setNombre("Destinatario");

        when(managerUserSession.usuarioLogeado()).thenReturn(remitente.getId());
        when(usuarioService.buscarUsuarioPorId(Mockito.anyList(), Mockito.eq(remitente.getId()))).thenReturn(remitente);
        when(usuarioService.buscarUsuarioPorId(Mockito.anyList(), Mockito.eq(destinatario.getId()))).thenReturn(destinatario);
    }

    @Test
    public void testEnviarMensajeArrendatario() throws Exception {
        mockMvc.perform(post("/chat/arrendatario/enviar")
                        .param("remitenteId", remitente.getId().toString())
                        .param("destinatarioId", destinatario.getId().toString())
                        .param("contenido", "Hola"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/arrendatario/" + destinatario.getId()));
    }

    @Test
    public void testEnviarMensajeArrendador() throws Exception {
        mockMvc.perform(post("/chat/arrendador/enviar")
                        .param("remitenteId", remitente.getId().toString())
                        .param("destinatarioId", destinatario.getId().toString())
                        .param("contenido", "Hola"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/arrendador/" + destinatario.getId()));
    }
}
