package autobnb.repository;

import autobnb.model.Marca;
import autobnb.model.Transmision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TransmisionRepository extends PagingAndSortingRepository<Transmision, Long>, JpaSpecificationExecutor {
    Optional<Transmision> findByNombre(String nombre);
    Page<Transmision> findAll(Pageable pageable);
}
