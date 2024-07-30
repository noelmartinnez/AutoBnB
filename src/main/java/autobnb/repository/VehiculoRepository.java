package autobnb.repository;

import autobnb.model.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends PagingAndSortingRepository<Vehiculo, Long>, JpaSpecificationExecutor<Vehiculo> {
    Optional<Vehiculo> findByMatricula(String matricula);
    List<Vehiculo> findByUsuarioId(Long usuarioId);
    Page<Vehiculo> findByMarcaNombreAndEnMantenimientoFalse(String marca, Pageable pageable);
    Page<Vehiculo> findByMarcaNombreAndModeloNombreAndEnMantenimientoFalse(String marca, String modelo, Pageable pageable);
    @Query("SELECT v FROM Vehiculo v WHERE v.oferta IS NOT NULL AND v.enMantenimiento = false")
    Page<Vehiculo> findAllWithOfertaAndEnMantenimientoFalse(Pageable pageable);
    Page<Vehiculo> findByMarcaNombreAndOfertaIsNotNullAndEnMantenimientoFalse(String marca, Pageable pageable);
    Page<Vehiculo> findByMarcaNombreAndModeloNombreAndOfertaIsNotNullAndEnMantenimientoFalse(String marca, String modelo, Pageable pageable);
    Page<Vehiculo> findByUsuarioId(Long usuarioId, Pageable pageable);
    @Query("SELECT v FROM Vehiculo v WHERE v.enMantenimiento = false")
    Page<Vehiculo> findAllWithoutMaintenance(Pageable pageable);
    @Query("SELECT DISTINCT v FROM Vehiculo v JOIN v.alquileres a WHERE a.pago.usuario.id = :usuarioId AND a.fechaDevolucion >= CURRENT_DATE")
    Page<Vehiculo> findVehiculosAlquiladosPorUsuario(Long usuarioId, Pageable pageable);
    @Query("SELECT DISTINCT v FROM Vehiculo v JOIN v.alquileres a WHERE v.usuario.id = :usuarioId AND a.fechaDevolucion >= CURRENT_DATE")
    Page<Vehiculo> findVehiculosConAlquileresActivosPorPropietario(Long usuarioId, Pageable pageable);
}
