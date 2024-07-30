package autobnb.repository;

import autobnb.model.Mensaje;
import autobnb.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class MensajeTest {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario remitente;
    private Usuario destinatario;

    @BeforeEach
    @Transactional
    public void setUp() {
        mensajeRepository.deleteAll();
        usuarioRepository.deleteAll();

        remitente = new Usuario("Juan Pérez", "juan.perez@gmail.com", "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, "12345678A", new Date(), new Date(), false, false, false, null, null);
        destinatario = new Usuario("María García", "maria.garcia@gmail.com", "87654321", 987654321, "Avenida Real 456",
                "Villa Real", 67890, "87654321B", new Date(), new Date(), false, false, false, null, null);
        usuarioRepository.save(remitente);
        usuarioRepository.save(destinatario);

        Mensaje mensaje1 = new Mensaje();
        mensaje1.setRemitente(remitente);
        mensaje1.setDestinatario(destinatario);
        mensaje1.setContenido("Hola María");
        mensaje1.setTimestamp(LocalDateTime.now());
        mensajeRepository.save(mensaje1);

        Mensaje mensaje2 = new Mensaje();
        mensaje2.setRemitente(destinatario);
        mensaje2.setDestinatario(remitente);
        mensaje2.setContenido("Hola Juan");
        mensaje2.setTimestamp(LocalDateTime.now());
        mensajeRepository.save(mensaje2);
    }

    @Test
    @Transactional
    public void testFindByRemitenteAndDestinatario() {
        List<Mensaje> mensajes = mensajeRepository.findByRemitenteAndDestinatarioOrRemitenteAndDestinatario(remitente, destinatario, destinatario, remitente);

        assertThat(mensajes).hasSize(2);
    }
}
