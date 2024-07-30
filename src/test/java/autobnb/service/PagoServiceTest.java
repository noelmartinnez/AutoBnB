package autobnb.service;

import autobnb.model.Alquiler;
import autobnb.model.Cuenta;
import autobnb.model.Pago;
import autobnb.model.Usuario;
import autobnb.repository.AlquilerRepository;
import autobnb.repository.PagoRepository;
import autobnb.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class PagoServiceTest {

    @Autowired
    private PagoService pagoService;

    @MockBean
    private PagoRepository pagoRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private AlquilerRepository alquilerRepository;

    private Pago pago;
    private Usuario usuario;
    private Alquiler alquiler;
    private Cuenta cuenta;

    @BeforeEach
    public void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldo(new BigDecimal("1000"));

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCuenta(cuenta); // Asociar la cuenta al usuario

        pago = new Pago("Titular", "123456789", usuario);
        pago.setId(1L);

        alquiler = new Alquiler();
        alquiler.setId(1L);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        Mockito.when(alquilerRepository.findById(1L)).thenReturn(Optional.of(alquiler));
    }

    @Test
    public void testCrearPago() {
        Mockito.when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago nuevoPago = pagoService.crearPago("Titular", "123456789", new BigDecimal("100"), 1L);
        assertThat(nuevoPago).isNotNull();
    }

    @Test
    public void testCrearPagoSaldoInsuficiente() {
        usuario.getCuenta().setSaldo(new BigDecimal("50"));

        Pago nuevoPago = pagoService.crearPago("Titular", "123456789", new BigDecimal("100"), 1L);
        assertThat(nuevoPago).isNull();
    }

    @Test
    public void testAsociarAlquilerAPago() {
        Mockito.when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        Pago pagoActualizado = pagoService.asociarAlquilerAPago(1L, 1L);
        assertThat(pagoActualizado).isNotNull();
        assertThat(pagoActualizado.getAlquiler()).isEqualTo(alquiler);
    }
}
