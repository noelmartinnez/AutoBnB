package autobnb.service;

import autobnb.dto.ModeloData;
import autobnb.model.Marca;
import autobnb.model.Modelo;
import autobnb.repository.MarcaRepository;
import autobnb.repository.ModeloRepository;
import autobnb.service.exception.ModeloServiceException;
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
public class ModeloService {
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Transactional(readOnly = true)
    public List<Modelo> listadoCompleto() {
        return ((List<Modelo>) modeloRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Modelo::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModeloData findById(Long modeloId) {
        Modelo modelo = modeloRepository.findById(modeloId).orElse(null);

        if (modelo == null) return null;
        else {
            return modelMapper.map(modelo, ModeloData.class);
        }
    }

    @Transactional(readOnly = true)
    public Modelo buscarModeloPorId(List<Modelo> modelos, Long idBuscado) {
        for (Modelo modelo : modelos) {
            if (modelo.getId().equals(idBuscado)) {
                return modelo;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ModeloData findByNombre(String nombre) {
        Modelo modelo = modeloRepository.findByNombre(nombre).orElse(null);

        if (modelo == null) return null;
        else {
            return modelMapper.map(modelo, ModeloData.class);
        }
    }

    @Transactional
    public void actualizarModelo(Long modeloId, ModeloData modeloData) {
        Modelo modelo = modeloRepository.findById(modeloId).orElse(null);

        if (modelo != null) {
            modelo.setNombre(modeloData.getNombre());
            modeloRepository.save(modelo);
        } else {
            throw new EntityNotFoundException("No se encontró el modelo con ID: " + modeloId);
        }
    }

    @Transactional(readOnly = true)
    public boolean tieneVehiculosAsociados(Long modeloId) {
        return modeloRepository.findById(modeloId)
                .map(modelo -> !modelo.getVehiculos().isEmpty())
                .orElse(false);
    }

    @Transactional
    public void eliminarModelo(Long modeloId) {
        Modelo modelo = modeloRepository.findById(modeloId).orElse(null);

        if (modelo != null && modelo.getVehiculos().isEmpty()) {
            modeloRepository.delete(modelo);
        } else if (modelo != null && !modelo.getVehiculos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el modelo porque tiene vehículos asociados.");
        } else {
            throw new EntityNotFoundException("No se encontró el modelo con ID: " + modeloId);
        }
    }

    @Transactional
    public ModeloData crearModelo(ModeloData modelo) {
        Optional<Modelo> modeloBD = modeloRepository.findByNombre(modelo.getNombre());
        if (modeloBD.isPresent())
            throw new ModeloServiceException("El modelo " + modelo.getNombre() + " ya está registrada.");
        else {
            Modelo modeloNuevo = modelMapper.map(modelo, Modelo.class);

            Marca marca = marcaRepository.findById(modelo.getIdMarca()).orElseThrow(() -> new RuntimeException("Marca no encontrada"));
            modeloNuevo.setNombre(modelo.getNombre());
            modeloNuevo.setMarca(marca);

            modeloNuevo = modeloRepository.save(modeloNuevo);
            return modelMapper.map(modeloNuevo, ModeloData.class);
        }
    }

    // Método para buscar modelos por ID de marca
    @Transactional(readOnly = true)
    public List<ModeloData> findByMarcaId(Long marcaId) {
        List<Modelo> modelos = modeloRepository.findByMarcaId(marcaId);
        return modelos.stream().map(modelo -> modelMapper.map(modelo, ModeloData.class)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Modelo> listadoPaginado(Pageable pageable) {
        return modeloRepository.findAll(pageable);
    }
}
