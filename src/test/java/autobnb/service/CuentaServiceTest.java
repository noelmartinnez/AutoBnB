package autobnb.service;

import autobnb.dto.CuentaData;
import autobnb.model.Cuenta;
import autobnb.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class CuentaServiceTest {

    @Autowired
    private CuentaService cuentaService;

    @MockBean
    private CuentaRepository cuentaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Cuenta cuenta;
    private CuentaData cuentaData;

    @BeforeEach
    public void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("123456789");
        cuenta.setSaldo(new BigDecimal("1000"));

        cuentaData = modelMapper.map(cuenta, CuentaData.class);

        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
    }

    @Test
    public void testCrearCuenta() {
        Mockito.when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        CuentaData nuevaCuenta = cuentaService.crearCuenta(cuentaData);
        assertThat(nuevaCuenta).isNotNull();
    }

    @Test
    public void testListarCuentasPaginado() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Cuenta> page = new PageImpl<>(Collections.singletonList(cuenta));

        Mockito.when(cuentaRepository.findAll(pageable)).thenReturn(page);

        Page<Cuenta> result = cuentaService.listadoPaginado(pageable);
        assertThat(result).hasSize(1);
    }

    @Test
    public void testFindById() {
        CuentaData result = cuentaService.findById(1L);
        assertThat(result).isNotNull();
    }
}
