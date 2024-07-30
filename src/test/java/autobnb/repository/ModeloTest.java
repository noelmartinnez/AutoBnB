package autobnb.repository;

import autobnb.model.Marca;
import autobnb.model.Modelo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql") // Asegúrate de que el script limpia la BD
public class ModeloTest {

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    private Marca toyota;

    @BeforeEach
    @Transactional
    public void setUp() {
        modeloRepository.deleteAll();
        marcaRepository.deleteAll();

        toyota = new Marca("Toyota");
        marcaRepository.save(toyota);

        modeloRepository.save(new Modelo("Corolla", toyota));
        modeloRepository.save(new Modelo("Camry", toyota));
    }

    @Test
    @Transactional
    public void testFindByNombre() {
        // WHEN
        Optional<Modelo> modeloOpt = modeloRepository.findByNombre("Corolla");

        // THEN
        assertThat(modeloOpt).isPresent();
        assertThat(modeloOpt.get().getNombre()).isEqualTo("Corolla");
    }

    @Test
    @Transactional
    public void testFindByMarcaId() {
        // WHEN
        List<Modelo> modelos = modeloRepository.findByMarcaId(toyota.getId());

        // THEN
        assertThat(modelos).hasSize(2);
    }

    @Test
    @Transactional
    public void testFindAll() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Modelo> modelos = modeloRepository.findAll(pageable);

        // THEN
        assertThat(modelos.getTotalElements()).isEqualTo(2);
        assertThat(modelos.getContent()).hasSize(2);
    }
}
