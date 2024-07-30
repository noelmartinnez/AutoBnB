package autobnb.service;

import autobnb.model.*;
import autobnb.repository.AlquilerRepository;
import autobnb.repository.PagoRepository;
import autobnb.repository.UsuarioRepository;
import autobnb.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerService {
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private PagoRepository pagoRepository;

    @Transactional
    public void eliminarAlquiler(Long alquilerId) {
        Alquiler alquiler = alquilerRepository.findById(alquilerId).orElse(null);
        if (alquiler != null) {
            Pago pago = alquiler.getPago();
            Vehiculo vehiculo = alquiler.getVehiculo();

            BigDecimal precioFinal = alquiler.getPrecioFinal();

            if (vehiculo != null) {
                Usuario arrendador = vehiculo.getUsuario();

                // Restar el precio final al saldo del arrendador
                if (arrendador != null && arrendador.getCuenta() != null) {
                    Cuenta cuentaArrendador = arrendador.getCuenta();
                    cuentaArrendador.setSaldo(cuentaArrendador.getSaldo().subtract(precioFinal));
                    usuarioRepository.save(arrendador); // Guardar cambios del usuario con su cuenta
                }

                vehiculo.getAlquileres().remove(alquiler);
                vehiculoRepository.save(vehiculo);
            }

            if (pago != null) {
                Usuario arrendatario = pago.getUsuario();

                // Devolver el precio final al saldo del arrendatario
                if (arrendatario != null && arrendatario.getCuenta() != null) {
                    Cuenta cuentaArrendatario = arrendatario.getCuenta();
                    cuentaArrendatario.setSaldo(cuentaArrendatario.getSaldo().add(precioFinal));
                    usuarioRepository.save(arrendatario); // Guardar cambios del usuario con su cuenta
                }
            }

            alquilerRepository.delete(alquiler);
        }
    }

    @Transactional(readOnly = true)
    public List<Alquiler> listadoCompleto() {
        return ((List<Alquiler>) alquilerRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Alquiler::getId))
                .collect(Collectors.toList());
    }

    /**
     * Crea y guarda un nuevo alquiler en la base de datos.
     *
     * @param fechaCreacion Fecha en que se crea el alquiler.
     * @param fechaEntrega Fecha de entrega del vehículo.
     * @param fechaDevolucion Fecha de devolución del vehículo.
     * @param precioFinal Precio final del alquiler.
     * @param vehiculoId ID del vehículo asociado al alquiler.
     * @param pagoId ID del pago asociado al alquiler.
     * @return El alquiler creado y guardado, o null si el vehículo o el pago no existen.
     */
    @Transactional
    public Alquiler crearAlquiler(Date fechaCreacion, Date fechaEntrega, Date fechaDevolucion, BigDecimal precioFinal, BigDecimal litrosCombustibles, Long vehiculoId, Long pagoId) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(vehiculoId);
        Optional<Pago> pago = pagoRepository.findById(pagoId);

        if (!vehiculo.isPresent() || !pago.isPresent()) {
            return null;
        }

        Alquiler nuevoAlquiler = new Alquiler(fechaCreacion, fechaEntrega, fechaDevolucion, precioFinal, vehiculo.get(), pago.get());
        if (litrosCombustibles != null) {
            nuevoAlquiler.setLitrosCombustible(litrosCombustibles);
        }
        return alquilerRepository.save(nuevoAlquiler);
    }

    @Transactional(readOnly = true)
    public Page<Alquiler> listadoPaginado(Pageable pageable) {
        return alquilerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Alquiler> obtenerAlquileresActivosPorVehiculoId(Long vehiculoId) {
        Date today = new Date();
        return alquilerRepository.findByVehiculoIdAndFechaDevolucionGreaterThanEqual(vehiculoId, today);
    }

    @Transactional(readOnly = true)
    public List<Alquiler> obtenerAlquileresPorVehiculos(List<Vehiculo> vehiculos) {
        List<Long> vehiculoIds = vehiculos.stream()
                .map(Vehiculo::getId)
                .collect(Collectors.toList());
        return alquilerRepository.findByVehiculoIdIn(vehiculoIds);
    }
}
