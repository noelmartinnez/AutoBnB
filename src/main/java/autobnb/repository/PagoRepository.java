package autobnb.repository;

import autobnb.model.Pago;
import autobnb.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends CrudRepository<Pago, Long> {
    Optional<Pago> findByUsuario(Usuario usuario);
    List<Pago> findByUsuarioId(Long usuarioId);
}
