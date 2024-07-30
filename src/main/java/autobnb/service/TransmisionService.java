package autobnb.service;

import autobnb.dto.TransmisionData;
import autobnb.model.Marca;
import autobnb.model.Transmision;
import autobnb.repository.TransmisionRepository;
import autobnb.service.exception.TransmisionServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransmisionService {
    @Autowired
    private TransmisionRepository transmisionRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Transmision> listadoCompleto() {
        return ((List<Transmision>) transmisionRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Transmision::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransmisionData findById(Long transmisionId) {
        Transmision transmision = transmisionRepository.findById(transmisionId).orElse(null);

        if (transmision == null) return null;
        else {
            return modelMapper.map(transmision, TransmisionData.class);
        }
    }

    @Transactional(readOnly = true)
    public Transmision buscarTransmisionPorId(List<Transmision> transmisiones, Long idBuscado) {
        for (Transmision transmision : transmisiones) {
            if (transmision.getId().equals(idBuscado)) {
                return transmision;
            }
        }
        return null;
    }

    @Transactional
    public void actualizarTransmision(Long transmisionId, TransmisionData transmisionData) {
        Transmision transmision = transmisionRepository.findById(transmisionId).orElse(null);

        if (transmision != null) {
            transmision.setNombre(transmisionData.getNombre());
            transmisionRepository.save(transmision);
        } else {
            throw new EntityNotFoundException("No se encontró la transmisión con ID: " + transmisionId);
        }
    }

    @Transactional(readOnly = true)
    public boolean tieneVehiculosAsociados(Long transmisionId) {
        return transmisionRepository.findById(transmisionId)
                .map(transmision -> !transmision.getVehiculos().isEmpty())
                .orElse(false);
    }

    @Transactional
    public void eliminarTransmision(Long transmisionId) {
        Transmision transmision = transmisionRepository.findById(transmisionId).orElse(null);

        if (transmision != null && transmision.getVehiculos().isEmpty()) {
            transmisionRepository.delete(transmision);
        } else if (transmision != null && !transmision.getVehiculos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la transmisión porque tiene vehículos asociados.");
        } else {
            throw new EntityNotFoundException("No se encontró la transmisión con ID: " + transmisionId);
        }
    }

    @Transactional(readOnly = true)
    public TransmisionData findByNombre(String nombre) {
        Transmision transmision = transmisionRepository.findByNombre(nombre).orElse(null);

        if (transmision == null) return null;
        else {
            return modelMapper.map(transmision, TransmisionData.class);
        }
    }

    @Transactional
    public TransmisionData crearTransmision(TransmisionData transmision) {
        Optional<Transmision> transmisionBD = transmisionRepository.findByNombre(transmision.getNombre());
        if (transmisionBD.isPresent())
            throw new TransmisionServiceException("La transmisión " + transmision.getNombre() + " ya está registrada.");
        else {
            Transmision transmisionNuevo = modelMapper.map(transmision, Transmision.class);
            transmisionNuevo = transmisionRepository.save(transmisionNuevo);
            return modelMapper.map(transmisionNuevo, TransmisionData.class);
        }
    }

    @Transactional(readOnly = true)
    public Page<Transmision> listadoPaginado(Pageable pageable) {
        return transmisionRepository.findAll(pageable);
    }
}
