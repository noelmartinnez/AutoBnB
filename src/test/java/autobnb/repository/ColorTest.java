package autobnb.repository;

import autobnb.model.Color;
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
public class ColorTest {

    @Autowired
    private ColorRepository colorRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        colorRepository.deleteAll();
        colorRepository.save(new Color("Rojo"));
        colorRepository.save(new Color("Azul"));
    }

    @Test
    @Transactional
    public void testFindByNombre() {
        // WHEN
        Optional<Color> colorOpt = colorRepository.findByNombre("Rojo");

        // THEN
        assertThat(colorOpt).isPresent();
        assertThat(colorOpt.get().getNombre()).isEqualTo("Rojo");
    }

    @Test
    @Transactional
    public void testFindAll() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Color> colores = colorRepository.findAll(pageable);

        // THEN
        assertThat(colores.getTotalElements()).isEqualTo(2);
        assertThat(colores.getContent()).hasSize(2);
    }
}
