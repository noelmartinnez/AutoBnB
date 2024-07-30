package autobnb.service;

import autobnb.dto.ColorData;
import autobnb.model.Color;
import autobnb.model.Marca;
import autobnb.repository.ColorRepository;
import autobnb.service.exception.ColorServiceException;
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
public class ColorService {
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<Color> listadoCompleto() {
        List<Color> coloresSinOrdenar = (List<Color>) colorRepository.findAll();
        return coloresSinOrdenar.stream()
                .sorted(Comparator.comparingLong(Color::getId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColorData findById(Long colorId) {
        Color color = colorRepository.findById(colorId).orElse(null);

        if (color == null) return null;
        else {
            return modelMapper.map(color, ColorData.class);
        }
    }

    @Transactional(readOnly = true)
    public Color buscarColorPorId(List<Color> colores, Long idBuscado) {
        for (Color color : colores) {
            if (color.getId().equals(idBuscado)) {
                return color;
            }
        }
        return null;
    }

    @Transactional
    public void actualizarColor(Long colorId, ColorData colorData) {
        Color color = colorRepository.findById(colorId).orElse(null);

        if (color != null) {
            color.setNombre(colorData.getNombre());
            colorRepository.save(color);
        } else {
            throw new EntityNotFoundException("No se encontró el color con ID: " + colorId);
        }
    }

    @Transactional(readOnly = true)
    public boolean tieneVehiculosAsociados(Long colorId) {
        return colorRepository.findById(colorId)
                .map(color -> !color.getVehiculos().isEmpty())
                .orElse(false);
    }

    @Transactional
    public void eliminarColor(Long colorId) {
        Color color = colorRepository.findById(colorId).orElse(null);

        if (color != null && color.getVehiculos().isEmpty()) {
            colorRepository.delete(color);
        } else if (color != null && !color.getVehiculos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el color porque tiene vehículos asociados.");
        } else {
            throw new EntityNotFoundException("No se encontró el color con ID: " + colorId);
        }
    }

    @Transactional(readOnly = true)
    public ColorData findByNombre(String nombre) {
        Color color = colorRepository.findByNombre(nombre).orElse(null);

        if (color == null) return null;
        else {
            return modelMapper.map(color, ColorData.class);
        }
    }

    @Transactional
    public ColorData crearColor(ColorData color) {
        Optional<Color> colorBD = colorRepository.findByNombre(color.getNombre());
        if (colorBD.isPresent())
            throw new ColorServiceException("El color " + color.getNombre() + " ya está registrado.");
        else {
            Color colorNuevo = modelMapper.map(color, Color.class);
            colorNuevo = colorRepository.save(colorNuevo);
            return modelMapper.map(colorNuevo, ColorData.class);
        }
    }

    @Transactional(readOnly = true)
    public Page<Color> listadoPaginado(Pageable pageable) {
        return colorRepository.findAll(pageable);
    }
}
