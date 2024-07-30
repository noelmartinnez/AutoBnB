package autobnb.service;

import autobnb.model.Alquiler;
import autobnb.model.Pago;
import autobnb.model.Usuario;
import autobnb.repository.AlquilerRepository;
import autobnb.repository.PagoRepository;
import autobnb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Crea y guarda un nuevo pago en la base de datos.
     *
     * @param titular Nombre del titular de la tarjeta.
     * @param numeroTarjeta Número de la tarjeta de crédito o débito.
     * @param usuarioId ID del Usuario asociado al pago.
     * @return El pago creado y guardado.
     */
    @Transactional
    public Pago crearPago(String titular, String numeroTarjeta, BigDecimal precioFinal, Long usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (!usuarioOpt.isPresent()) {
            return null; // Usuario no encontrado
        }

        Usuario usuario = usuarioOpt.get();

        // Verifica que el saldo en la cuenta sea suficiente
        if (usuario.getCuenta().getSaldo().compareTo(precioFinal) < 0) {
            return null; // Saldo insuficiente
        }

        // Si hay saldo suficiente, crea el pago y actualiza el saldo
        usuario.getCuenta().setSaldo(usuario.getCuenta().getSaldo().subtract(precioFinal));
        usuarioRepository.save(usuario); // Guarda el usuario con el saldo actualizado

        Pago nuevoPago = new Pago(titular, numeroTarjeta, usuario);
        return pagoRepository.save(nuevoPago);
    }

    /**
     * Asocia un alquiler a un pago dado el ID de ambos.
     *
     * @param pagoId ID del pago a actualizar.
     * @param alquilerId ID del alquiler a asociar.
     * @return El pago actualizado o null si no se encuentra el pago o el alquiler.
     */
    @Transactional
    public Pago asociarAlquilerAPago(Long pagoId, Long alquilerId) {
        Pago pago = pagoRepository.findById(pagoId).orElse(null);
        Alquiler alquiler = alquilerRepository.findById(alquilerId).orElse(null);

        if (pago != null && alquiler != null) {
            pago.setAlquiler(alquiler);
            return pagoRepository.save(pago);
        }

        return null; // Retorna null si alguno no se encuentra
    }
}
