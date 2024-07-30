package autobnb.repository;

import autobnb.model.Modelo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository extends PagingAndSortingRepository<Modelo, Long>, JpaSpecificationExecutor {
    Optional<Modelo> findByNombre(String nombre);
    List<Modelo> findByMarcaId(Long marcaId);
    Page<Modelo> findAll(Pageable pageable);
}
