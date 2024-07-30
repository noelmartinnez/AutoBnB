package autobnb.repository;

import autobnb.model.Cuenta;
import autobnb.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class CuentaTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    @Transactional
    public void setUp() throws Exception {
        cuentaRepository.deleteAll();
        usuarioRepository.deleteAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Usuario usuario = new Usuario("Juan Pérez", "juan.perez@gmail.com", "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, "12345678A", sdf.parse("2025-12-31"), sdf.parse("2025-12-31"), false, false, false, null, null);
        usuarioRepository.save(usuario);

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("ES12345678901234567890");
        cuenta.setSaldo(new BigDecimal("1000.00"));
        cuenta.setUsuario(usuario);
        cuentaRepository.save(cuenta);
    }

    @Test
    @Transactional
    public void testFindByUsuario() {
        Usuario usuario = usuarioRepository.findByEmail("juan.perez@gmail.com").orElse(null);

        Cuenta cuenta = cuentaRepository.findByUsuario(usuario).orElse(null);

        assertThat(cuenta).isNotNull();
        assertThat(cuenta.getSaldo()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    @Transactional
    public void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cuenta> cuentas = cuentaRepository.findAll(pageable);

        assertThat(cuentas.getContent()).hasSize(1);
    }
}
