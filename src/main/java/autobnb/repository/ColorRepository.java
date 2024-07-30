package autobnb.repository;

import autobnb.model.Color;
import autobnb.model.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ColorRepository extends PagingAndSortingRepository<Color, Long>, JpaSpecificationExecutor {
    Optional<Color> findByNombre(String nombre);
    Page<Color> findAll(Pageable pageable);
}
