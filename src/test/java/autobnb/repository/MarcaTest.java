package autobnb.repository;

import autobnb.model.Marca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql") // Asegúrate de que el script limpia la BD
public class MarcaTest {

    @Autowired
    private MarcaRepository marcaRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        marcaRepository.deleteAll();
        marcaRepository.save(new Marca("Toyota"));
        marcaRepository.save(new Marca("Ford"));
    }

    @Test
    @Transactional
    public void testFindByNombre() {
        // WHEN
        Optional<Marca> marcaOpt = marcaRepository.findByNombre("Toyota");

        // THEN
        assertThat(marcaOpt).isPresent();
        assertThat(marcaOpt.get().getNombre()).isEqualTo("Toyota");
    }

    @Test
    @Transactional
    public void testFindAll() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Marca> marcas = marcaRepository.findAll(pageable);

        // THEN
        assertThat(marcas.getTotalElements()).isEqualTo(2);
        assertThat(marcas.getContent()).hasSize(2);
    }
}
