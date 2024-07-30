package autobnb.repository;

import autobnb.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AlquilerRepository extends PagingAndSortingRepository<Alquiler, Long>, JpaSpecificationExecutor {
    Optional<Alquiler> findByPago(Pago pago);
    Optional<Alquiler> findByVehiculo(Vehiculo vehiculo);
    List<Alquiler> findByVehiculoId(Long vehiculoId);
    Page<Alquiler> findAll(Pageable pageable);
    List<Alquiler> findByVehiculoIdAndFechaDevolucionGreaterThanEqual(Long vehiculoId, Date fechaDevolucion);
    List<Alquiler> findByVehiculoIdIn(List<Long> vehiculoIds);
}
