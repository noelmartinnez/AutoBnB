package autobnb.repository;

import autobnb.model.Marca;
import autobnb.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>, JpaSpecificationExecutor {
    Optional<Usuario> findByEmail(String s);
    Optional<Usuario> findByDni(String dni);
    Page<Usuario> findAll(Pageable pageable);
}
