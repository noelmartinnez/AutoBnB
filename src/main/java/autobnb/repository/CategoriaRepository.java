package autobnb.repository;

import autobnb.model.Categoria;
import autobnb.model.Marca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CategoriaRepository extends PagingAndSortingRepository<Categoria, Long>, JpaSpecificationExecutor {
    Optional<Categoria> findByNombre(String nombre);
    Page<Categoria> findAll(Pageable pageable);
}
