package autobnb.repository;

import autobnb.model.Comentario;
import autobnb.model.Marca;
import autobnb.model.Usuario;
import autobnb.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ComentarioRepository extends PagingAndSortingRepository<Comentario, Long>, JpaSpecificationExecutor {
    Optional<Comentario> findByUsuario(Usuario usuario);
    Optional<Comentario> findByVehiculo(Vehiculo vehiculo);
    List<Comentario> findByUsuarioId(Long usuarioId);
    List<Comentario> findByVehiculoId(Long vehiculoId);
    Page<Comentario> findAll(Pageable pageable);
}
