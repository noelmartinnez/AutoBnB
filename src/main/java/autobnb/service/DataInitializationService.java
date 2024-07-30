package autobnb.service;

import autobnb.dto.ModeloData;
import autobnb.model.Marca;
import autobnb.model.Modelo;
import autobnb.repository.MarcaRepository;
import autobnb.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class DataInitializationService {
    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private MarcaService marcaService;

    @Autowired
    private ModeloService modeloService;

    private String sanitizeMakeName(String makeName) {
        // Eliminar caracteres especiales y reemplazar espacios por signos +
        return makeName.replaceAll("[^a-zA-Z0-9]", "").replace(" ", "+");
    }

    /* https://vpic.nhtsa.dot.gov/api/vehicles/getallmakes?format=json */
    private static final String MODELS_API_URL = "https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMake/";

    public void initializeData() {
        /*RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            for (Marca mar : marcaService.listadoCompleto()) {
                // Verificar si la marca ya existe
                Optional<Marca> marcaOptional = marcaRepository.findByNombre(mar.getNombre());
                Marca marca;

                if (marcaOptional.isPresent()) {
                    marca = marcaOptional.get();
                } else {
                    // Crear y guardar la nueva entidad Marca
                    marca = new Marca(mar.getNombre());
                    marcaRepository.save(marca);
                }

                // Limpiar el nombre de la marca
                String sanitizedMakeName = sanitizeMakeName(mar.getNombre());

                // Obtener los modelos de cada marca
                String modelsResponse = restTemplate.getForObject(MODELS_API_URL + sanitizedMakeName + "?format=json", String.class);
                JsonNode modelsJson = objectMapper.readTree(modelsResponse).get("Results");

                for (JsonNode modeloNode : modelsJson) {
                    if (!modeloNode.has("Make_Name") || !modeloNode.get("Make_Name").asText().equalsIgnoreCase(mar.getNombre())) {
                        continue; // Omite modelos que no sean de la marca exacta
                    }

                    String modeloName = modeloNode.get("Model_Name").asText();

                    Optional<Modelo> existingModel = modeloRepository.findByNombre(modeloName);
                    if (!existingModel.isPresent()) {
                        ModeloData modeloData = new ModeloData();
                        modeloData.setNombre(modeloName);
                        modeloData.setIdMarca(marca.getId());

                        // Guardar la entidad Modelo
                        modeloService.crearModelo(modeloData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
