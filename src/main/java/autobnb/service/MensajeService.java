package autobnb.service;

import autobnb.model.Mensaje;
import autobnb.model.Usuario;
import autobnb.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Transactional
    public Mensaje enviarMensaje(Usuario remitente, Usuario destinatario, String contenido) {
        Mensaje mensaje = new Mensaje();
        mensaje.setRemitente(remitente);
        mensaje.setDestinatario(destinatario);
        mensaje.setContenido(contenido);
        mensaje.setTimestamp(LocalDateTime.now());

        return mensajeRepository.save(mensaje);
    }

    @Transactional(readOnly = true)
    public List<Mensaje> obtenerConversacion(Usuario usuario1, Usuario usuario2) {
        return mensajeRepository.findByRemitenteAndDestinatarioOrRemitenteAndDestinatario(usuario1, usuario2, usuario2, usuario1);
    }
}
