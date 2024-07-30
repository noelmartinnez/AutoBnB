package autobnb.service;

import autobnb.dto.MarcaData;
import autobnb.dto.UsuarioData;
import autobnb.model.Marca;
import autobnb.model.Modelo;
import autobnb.model.Usuario;
import autobnb.repository.MarcaRepository;
import autobnb.service.exception.MarcaServiceException;
import autobnb.service.exception.UsuarioServiceException;
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
public class MarcaService {
    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Transactional(readOnly = true)
    public List<Marca> listadoCompleto() {
        return ((List<Marca>) marcaRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Marca::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MarcaData findById(Long marcaId) {
        Marca marca = marcaRepository.findById(marcaId).orElse(null);

        if (marca == null) return null;
        else {
            return modelMapper.map(marca, MarcaData.class);
        }
    }

    @Transactional(readOnly = true)
    public Marca buscarMarcaPorId(List<Marca> marcas, Long idBuscado) {
        for (Marca marca : marcas) {
            if (marca.getId().equals(idBuscado)) {
                return marca;
            }
        }
        return null;
    }

    @Transactional
    public void actualizarMarca(Long marcaId, MarcaData marcaData) {
        Marca marca = marcaRepository.findById(marcaId).orElse(null);

        if (marca != null) {
            marca.setNombre(marcaData.getNombre());
            marcaRepository.save(marca);
        } else {
            throw new EntityNotFoundException("No se encontró la marca con ID: " + marcaId);
        }
    }

    @Transactional(readOnly = true)
    public boolean tieneVehiculosAsociados(Long marcaId) {
        return marcaRepository.findById(marcaId)
                .map(marca -> !marca.getVehiculos().isEmpty())
                .orElse(false);
    }

    @Transactional
    public void eliminarMarca(Long marcaId) {
        Marca marca = marcaRepository.findById(marcaId).orElse(null);

        if (marca != null && marca.getVehiculos().isEmpty()) {
            marcaRepository.delete(marca);
        } else if (marca != null && !marca.getVehiculos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la marca porque tiene vehículos asociados.");
        } else {
            throw new EntityNotFoundException("No se encontró la marca con ID: " + marcaId);
        }
    }

    @Transactional(readOnly = true)
    public MarcaData findByNombre(String nombre) {
        Marca marca = marcaRepository.findByNombre(nombre).orElse(null);

        if (marca == null) return null;
        else {
            return modelMapper.map(marca, MarcaData.class);
        }
    }

    @Transactional
    public MarcaData crearMarca(MarcaData marca) {
        Optional<Marca> marcaBD = marcaRepository.findByNombre(marca.getNombre());
        if (marcaBD.isPresent())
            throw new MarcaServiceException("La marca " + marca.getNombre() + " ya está registrada.");
        else {
            Marca marcaNueva = modelMapper.map(marca, Marca.class);
            marcaNueva = marcaRepository.save(marcaNueva);
            return modelMapper.map(marcaNueva, MarcaData.class);
        }
    }

    @Transactional(readOnly = true)
    public Page<Marca> listadoPaginado(Pageable pageable) {
        return marcaRepository.findAll(pageable);
    }
}
