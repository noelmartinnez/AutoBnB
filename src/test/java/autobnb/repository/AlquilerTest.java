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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class AlquilerTest {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PagoRepository pagoRepository;

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
        alquilerRepository.deleteAll();
        vehiculoRepository.deleteAll();
        usuarioRepository.deleteAll();
        pagoRepository.deleteAll();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Usuario usuario = new Usuario("Juan Pérez", "juan.perez@gmail.com", "12345678", 123456789, "Calle Falsa 123",
                "Ciudad Ficticia", 12345, "12345678A", sdf.parse("2025-12-31"), sdf.parse("2025-12-31"), false, false, false, null, null);
        usuarioRepository.save(usuario);

        Pago pago = new Pago("Juan Pérez", "1234567812345678", usuario);
        pagoRepository.save(pago);

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

        Vehiculo vehiculo = new Vehiculo("Sedán compacto", "imagen1.jpg", "ABC1234", 50000, 2019, 5, 400, 4, 5, true,
                false, new BigDecimal("50.00"), new BigDecimal("25.00"), new BigDecimal("1.20"), usuario, marca, modelo, categoria, transmision, color);
        vehiculoRepository.save(vehiculo);

        Alquiler alquiler = new Alquiler(new Date(), sdf.parse("2024-12-01"), sdf.parse("2024-12-10"), new BigDecimal("500.00"), vehiculo, pago);
        alquilerRepository.save(alquiler);
    }

    @Test
    @Transactional
    public void testFindByPago() {
        Pago pago = pagoRepository.findByUsuario(usuarioRepository.findByEmail("juan.perez@gmail.com").orElse(null)).orElse(null);

        Optional<Alquiler> alquiler = alquilerRepository.findByPago(pago);

        assertThat(alquiler).isPresent();
        assertThat(alquiler.get().getPrecioFinal()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    @Transactional
    public void testFindByVehiculoId() {
        Vehiculo vehiculo = vehiculoRepository.findByMatricula("ABC1234").orElse(null);

        List<Alquiler> alquileres = alquilerRepository.findByVehiculoId(vehiculo.getId());

        assertThat(alquileres).hasSize(1);
    }
}
