package autobnb.service;

import autobnb.dto.CuentaData;
import autobnb.model.Cuenta;
import autobnb.model.Marca;
import autobnb.model.Usuario;
import autobnb.repository.CuentaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaService {
    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public CuentaData crearCuenta(CuentaData cuenta) {
        Cuenta cuentaNueva = modelMapper.map(cuenta, Cuenta.class);
        cuentaNueva = cuentaRepository.save(cuentaNueva);
        return modelMapper.map(cuentaNueva, CuentaData.class);
    }

    @Transactional(readOnly = true)
    public CuentaData findById(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElse(null);

        if (cuenta == null) return null;
        else {
            return modelMapper.map(cuenta, CuentaData.class);
        }
    }

    @Transactional(readOnly = true)
    public List<Cuenta> listadoCompleto() {
        return ((List<Cuenta>) cuentaRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Cuenta::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Cuenta> listadoPaginado(Pageable pageable) {
        return cuentaRepository.findAll(pageable);
    }
}
