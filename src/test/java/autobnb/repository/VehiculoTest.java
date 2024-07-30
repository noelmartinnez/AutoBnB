package autobnb.repository;

import autobnb.model.*;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql") // Asegúrate de que el script limpia la BD
public class VehiculoTest {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TransmisionRepository transmisionRepository;

    @Autowired
    private ColorRepository colorRepository;

    @BeforeEach
    @Transactional
    public void setUp() throws Exception {
        vehiculoRepository.deleteAll();
        usuarioRepository.deleteAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Usuario usuario = new Usuario("Juan Pérez", "juan.perez@gmail.com", "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, "12345678A", sdf.parse("2025-12-31"), sdf.parse("2025-12-31"), false, false, false, null, null);
        usuarioRepository.save(usuario);

        Marca marca = new Marca("Toyota");
        marcaRepository.save(marca);

        Modelo modelo = new Modelo("Corolla", marca);
        modeloRepository.save(modelo);

        Categoria categoria = new Categoria("Sedán");
        categoriaRepository.save(categoria);

        Transmision transmision = new Transmision("Automático");
        transmisionRepository.save(transmision);

        Color color = new Color("Rojo");
        colorRepository.save(color);

        Vehiculo vehiculo1 = new Vehiculo("Sedán compacto", "imagen1.jpg", "ABC1234", 50000, 2019, 5, 400, 4, 5, true,
                false, new BigDecimal("50.00"), new BigDecimal("25.00"), new BigDecimal("1.20"), usuario, marca, modelo, categoria, transmision, color);
        vehiculoRepository.save(vehiculo1);

        Vehiculo vehiculo2 = new Vehiculo("SUV", "imagen2.jpg", "DEF5678", 30000, 2020, 7, 600, 5, 6, false,
                true, new BigDecimal("70.00"), new BigDecimal("35.00"), new BigDecimal("1.30"), usuario, marca, modelo, categoria, transmision, color);
        vehiculoRepository.save(vehiculo2);
    }

    @Test
    @Transactional
    public void testFindByMatricula() {
        // WHEN
        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findByMatricula("ABC1234");

        // THEN
        assertThat(vehiculoOpt).isPresent();
        assertThat(vehiculoOpt.get().getDescripcion()).isEqualTo("Sedán compacto");
    }

    @Test
    @Transactional
    public void testFindByUsuarioId() {
        // GIVEN
        Usuario usuario = usuarioRepository.findByEmail("juan.perez@gmail.com").orElse(null);

        // WHEN
        List<Vehiculo> vehiculos = vehiculoRepository.findByUsuarioId(usuario.getId());

        // THEN
        assertThat(vehiculos).hasSize(2);
    }

    @Test
    @Transactional
    public void testFindByMarcaNombreAndEnMantenimientoFalse() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 2);
        Page<Vehiculo> vehiculos = vehiculoRepository.findByMarcaNombreAndEnMantenimientoFalse("Toyota", pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
    }

    @Test
    @Transactional
    public void testFindAllWithoutMaintenance() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 2);
        Page<Vehiculo> vehiculos = vehiculoRepository.findAllWithoutMaintenance(pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
        assertThat(vehiculos.getContent().get(0).getDescripcion()).isEqualTo("Sedán compacto");
    }

    @Test
    @Transactional
    public void testFindByMarcaNombreAndModeloNombreAndEnMantenimientoFalse() {
        // WHEN
        Pageable pageable = PageRequest.of(0, 2);
        Page<Vehiculo> vehiculos = vehiculoRepository.findByMarcaNombreAndModeloNombreAndEnMantenimientoFalse("Toyota", "Corolla", pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
        assertThat(vehiculos.getContent().get(0).getDescripcion()).isEqualTo("Sedán compacto");
    }

    @Test
    @Transactional
    public void testFindAllWithOfertaAndEnMantenimientoFalse() {
        // GIVEN
        Vehiculo vehiculo = vehiculoRepository.findByMatricula("ABC1234").orElse(null);
        if (vehiculo != null) {
            vehiculo.setOferta(10);
            vehiculoRepository.save(vehiculo);
        }

        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> vehiculos = vehiculoRepository.findAllWithOfertaAndEnMantenimientoFalse(pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
        assertThat(vehiculos.getContent().get(0).getOferta()).isEqualTo(10);
    }

    @Test
    @Transactional
    public void testFindByMarcaNombreAndOfertaIsNotNullAndEnMantenimientoFalse() {
        // GIVEN
        Vehiculo vehiculo = vehiculoRepository.findByMatricula("ABC1234").orElse(null);
        if (vehiculo != null) {
            vehiculo.setOferta(15);
            vehiculoRepository.save(vehiculo);
        }

        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> vehiculos = vehiculoRepository.findByMarcaNombreAndOfertaIsNotNullAndEnMantenimientoFalse("Toyota", pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
        assertThat(vehiculos.getContent().get(0).getOferta()).isEqualTo(15);
    }

    @Test
    @Transactional
    public void testFindByMarcaNombreAndModeloNombreAndOfertaIsNotNullAndEnMantenimientoFalse() {
        // GIVEN
        Vehiculo vehiculo = vehiculoRepository.findByMatricula("ABC1234").orElse(null);
        if (vehiculo != null) {
            vehiculo.setOferta(20);
            vehiculoRepository.save(vehiculo);
        }

        // WHEN
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehiculo> vehiculos = vehiculoRepository.findByMarcaNombreAndModeloNombreAndOfertaIsNotNullAndEnMantenimientoFalse("Toyota", "Corolla", pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
        assertThat(vehiculos.getContent().get(0).getOferta()).isEqualTo(20);
    }

    @Test
    @Transactional
    public void testFindByUsuarioIdWithPagination() {
        // GIVEN
        Usuario usuario = usuarioRepository.findByEmail("juan.perez@gmail.com").orElse(null);

        // WHEN
        Pageable pageable = PageRequest.of(0, 1);
        Page<Vehiculo> vehiculos = vehiculoRepository.findByUsuarioId(usuario.getId(), pageable);

        // THEN
        assertThat(vehiculos.getContent()).hasSize(1);
        assertThat(vehiculos.getTotalElements()).isEqualTo(2);
    }
}
