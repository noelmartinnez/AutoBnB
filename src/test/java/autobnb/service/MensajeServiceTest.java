package autobnb.service;

import autobnb.model.Mensaje;
import autobnb.model.Usuario;
import autobnb.repository.MensajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class MensajeServiceTest {

    @Autowired
    private MensajeService mensajeService;

    @MockBean
    private MensajeRepository mensajeRepository;

    private Usuario remitente;
    private Usuario destinatario;
    private Mensaje mensaje;

    @BeforeEach
    public void setUp() {
        remitente = new Usuario();
        remitente.setId(1L);

        destinatario = new Usuario();
        destinatario.setId(2L);

        mensaje = new Mensaje();
        mensaje.setId(1L);
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setContenido("Hola");
        mensaje.setTimestamp(LocalDateTime.now());

        Mockito.when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);
        Mockito.when(mensajeRepository.findByRemitenteAndDestinatarioOrRemitenteAndDestinatario(
                remitente, destinatario, destinatario, remitente)).thenReturn(Arrays.asList(mensaje));
    }

    @Test
    public void testEnviarMensaje() {
        Mensaje nuevoMensaje = mensajeService.enviarMensaje(remitente, destinatario, "Hola");
        assertThat(nuevoMensaje).isNotNull();
    }

    @Test
    public void testObtenerConversacion() {
        List<Mensaje> mensajes = mensajeService.obtenerConversacion(remitente, destinatario);
        assertThat(mensajes).hasSize(1);
    }
}
