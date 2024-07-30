package autobnb.repository;

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

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql") // Asegúrate de que el script limpia la BD
public class UsuarioTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario crearUsuarioValido(String email, String dni) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return new Usuario("Juan Gutiérrez", email, "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, dni, sdf.parse("2025-12-31"), sdf.parse("2025-12-31"), false, false, false, null, null);
    }

    @BeforeEach
    @Transactional
    public void setUp() throws Exception {
        usuarioRepository.deleteAll();

        // Insertar datos de prueba
        usuarioRepository.save(crearUsuarioValido("juan.gutierrez@gmail.com", "12345678A"));
        usuarioRepository.save(crearUsuarioValido("maria.rodriguez@gmail.com", "87654321B"));
    }

    @Test
    @Transactional
    public void testFindByEmail() {
        // WHEN
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail("juan.gutierrez@gmail.com");

        // THEN
        assertThat(usuarioOpt).isPresent();
        assertThat(usuarioOpt.get().getNombre()).isEqualTo("Juan Gutiérrez");
    }

    @Test
    @Transactional
    public void testFindByDni() {
        // WHEN
        Optional<Usuario> usuarioOpt = usuarioRepository.findByDni("87654321B");

        // THEN
        assertThat(usuarioOpt).isPresent();
        assertThat(usuarioOpt.get().getNombre()).isEqualTo("Juan Gutiérrez");
    }

    @Test
    @Transactional
    public void testFindAllWithPagination() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 1);
        Page<Usuario> page = usuarioRepository.findAll(pageable);

        // THEN
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1); // Solo un elemento debido al tamaño de la página
    }
}
