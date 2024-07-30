package autobnb.repository;

import autobnb.model.Cuenta;
import autobnb.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CuentaRepository extends PagingAndSortingRepository<Cuenta, Long>, JpaSpecificationExecutor {
    Optional<Cuenta> findByUsuario(Usuario usuario);
    Page<Cuenta> findAll(Pageable pageable);
}