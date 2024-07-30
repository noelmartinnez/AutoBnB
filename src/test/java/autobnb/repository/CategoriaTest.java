package autobnb.repository;

import autobnb.model.Categoria;
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
public class CategoriaTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        categoriaRepository.deleteAll();
        categoriaRepository.save(new Categoria("SUV"));
        categoriaRepository.save(new Categoria("Sedán"));
    }

    @Test
    @Transactional
    public void testFindByNombre() {
        // WHEN
        Optional<Categoria> categoriaOpt = categoriaRepository.findByNombre("SUV");

        // THEN
        assertThat(categoriaOpt).isPresent();
        assertThat(categoriaOpt.get().getNombre()).isEqualTo("SUV");
    }

    @Test
    @Transactional
    public void testFindAll() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> categorias = categoriaRepository.findAll(pageable);

        // THEN
        assertThat(categorias.getTotalElements()).isEqualTo(2);
        assertThat(categorias.getContent()).hasSize(2);
    }
}
