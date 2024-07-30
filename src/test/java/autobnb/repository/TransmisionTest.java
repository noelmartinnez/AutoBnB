package autobnb.repository;

import autobnb.model.Transmision;
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
public class TransmisionTest {

    @Autowired
    private TransmisionRepository transmisionRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        transmisionRepository.deleteAll();
        transmisionRepository.save(new Transmision("Manual"));
        transmisionRepository.save(new Transmision("Automático"));
    }

    @Test
    @Transactional
    public void testFindByNombre() {
        // WHEN
        Optional<Transmision> transmisionOpt = transmisionRepository.findByNombre("Manual");

        // THEN
        assertThat(transmisionOpt).isPresent();
        assertThat(transmisionOpt.get().getNombre()).isEqualTo("Manual");
    }

    @Test
    @Transactional
    public void testFindAll() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transmision> transmisiones = transmisionRepository.findAll(pageable);

        // THEN
        assertThat(transmisiones.getTotalElements()).isEqualTo(2);
        assertThat(transmisiones.getContent()).hasSize(2);
    }
}
