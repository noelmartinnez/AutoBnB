package autobnb.repository;

import autobnb.model.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface MarcaRepository extends PagingAndSortingRepository<Marca, Long>, JpaSpecificationExecutor {
    Optional<Marca> findByNombre(String nombre);
    Page<Marca> findAll(Pageable pageable);
}
