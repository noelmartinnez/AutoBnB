package autobnb.repository;

import autobnb.model.Mensaje;
import autobnb.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByRemitenteAndDestinatarioOrRemitenteAndDestinatario(Usuario remitente1, Usuario destinatario1, Usuario remitente2, Usuario destinatario2);
}
