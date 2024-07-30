package autobnb.repository;

import autobnb.model.Pago;
import autobnb.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql") // Asegúrate de que el script limpia la BD
public class PagoTest {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario crearUsuarioValido() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return new Usuario("Juan Gutiérrez", "juan.gutierrez@gmail.com", "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, "12345678A", sdf.parse("2025-12-31"), sdf.parse("2025-12-31"), false, false, false, null, null);
    }

    @Test
    @Transactional
    public void guardarYPago() throws Exception {
        // GIVEN
        // Un usuario en la base de datos
        Usuario usuario = crearUsuarioValido();
        usuarioRepository.save(usuario);

        // Creamos un objeto Pago
        Pago pago = new Pago("Juan Gutiérrez", "1234567812345678", usuario);

        // WHEN
        // Salvamos el pago en la base de datos
        pagoRepository.save(pago);

        // THEN
        // Se ha actualizado el ID
        assertThat(pago.getId()).isNotNull();

        // Podemos recuperar el pago con su ID
        Pago pagoBD = pagoRepository.findById(pago.getId()).orElse(null);
        assertThat(pagoBD).isNotNull();
        assertThat(pagoBD.getTitular()).isEqualTo("Juan Gutiérrez");
        assertThat(pagoBD.getNumeroTarjeta()).isEqualTo("1234567812345678");
        assertThat(pagoBD.getUsuario()).isEqualTo(usuario);
    }

    @Test
    @Transactional
    public void buscarPagoPorUsuario() throws Exception {
        // GIVEN
        // Un usuario y su pago asociado
        Usuario usuario = crearUsuarioValido();
        usuarioRepository.save(usuario);

        Pago pago = new Pago("Juan Gutiérrez", "1234567812345678", usuario);
        pagoRepository.save(pago);

        // WHEN
        // Buscamos el pago por usuario
        Optional<Pago> pagoBD = pagoRepository.findByUsuario(usuario);

        // THEN
        // Comprobamos que se ha recuperado correctamente
        assertThat(pagoBD).isPresent();
        assertThat(pagoBD.get().getTitular()).isEqualTo("Juan Gutiérrez");
    }

    @Test
    @Transactional
    public void buscarPagosPorUsuarioId() throws Exception {
        // GIVEN
        // Un usuario y múltiples pagos
        Usuario usuario = crearUsuarioValido();
        usuarioRepository.save(usuario);

        Pago pago1 = new Pago("Juan Gutiérrez", "1234567812345678", usuario);
        Pago pago2 = new Pago("Juan Gutiérrez", "8765432187654321", usuario);
        pagoRepository.save(pago1);
        pagoRepository.save(pago2);

        // WHEN
        // Buscamos todos los pagos por el ID del usuario
        List<Pago> pagos = pagoRepository.findByUsuarioId(usuario.getId());

        // THEN
        // Comprobamos que se han recuperado ambos pagos
        assertThat(pagos).hasSize(2);
        assertThat(pagos).contains(pago1, pago2);
    }
}
