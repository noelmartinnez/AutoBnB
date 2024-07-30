package autobnb.service;

import autobnb.dto.CategoriaData;
import autobnb.model.Categoria;
import autobnb.model.Marca;
import autobnb.repository.CategoriaRepository;
import autobnb.service.exception.CategoriaServiceException;
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
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Categoria> listadoCompleto() {
        return ((List<Categoria>) categoriaRepository.findAll())
                .stream()
                .sorted(Comparator.comparingLong(Categoria::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaData findById(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);

        if (categoria == null) return null;
        else {
            return modelMapper.map(categoria, CategoriaData.class);
        }
    }

    @Transactional(readOnly = true)
    public Categoria buscarCategoriaPorId(List<Categoria> categorias, Long idBuscado) {
        for (Categoria categoria : categorias) {
            if (categoria.getId().equals(idBuscado)) {
                return categoria;
            }
        }
        return null;
    }

    @Transactional
    public void actualizarCategoria(Long categoriaId, CategoriaData categoriaData) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);

        if (categoria != null) {
            categoria.setNombre(categoriaData.getNombre());
            categoria.setDescripcion(categoriaData.getDescripcion());
            categoriaRepository.save(categoria);
        } else {
            throw new EntityNotFoundException("No se encontró la categoria con ID: " + categoriaId);
        }
    }

    @Transactional(readOnly = true)
    public boolean tieneVehiculosAsociados(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .map(categoria -> !categoria.getVehiculos().isEmpty())
                .orElse(false);
    }

    @Transactional
    public void eliminarCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);

        if (categoria != null && categoria.getVehiculos().isEmpty()) {
            categoriaRepository.delete(categoria);
        } else if (categoria != null && !categoria.getVehiculos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoria porque tiene vehículos asociados.");
        } else {
            throw new EntityNotFoundException("No se encontró la categoria con ID: " + categoriaId);
        }
    }

    @Transactional(readOnly = true)
    public CategoriaData findByNombre(String nombre) {
        Categoria categoria = categoriaRepository.findByNombre(nombre).orElse(null);

        if (categoria == null) return null;
        else {
            return modelMapper.map(categoria, CategoriaData.class);
        }
    }

    @Transactional
    public CategoriaData crearCategoria(CategoriaData categoriaData) {
        Optional<Categoria> categoriaBD = categoriaRepository.findByNombre(categoriaData.getNombre());
        if (categoriaBD.isPresent())
            throw new CategoriaServiceException("La categoria " + categoriaData.getNombre() + " ya está registrada.");
        else {
            Categoria categoriaNueva = modelMapper.map(categoriaData, Categoria.class);
            categoriaNueva = categoriaRepository.save(categoriaNueva);
            return modelMapper.map(categoriaNueva, CategoriaData.class);
        }
    }

    @Transactional(readOnly = true)
    public Page<Categoria> listadoPaginado(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }
}
