package autobnb;

import autobnb.service.DataInitializationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final DataInitializationService dataInitializationService;

    public DataInitializer(DataInitializationService dataInitializationService) {
        this.dataInitializationService = dataInitializationService;
    }

    @Override
    public void run(String... args) throws Exception {
        dataInitializationService.initializeData();
    }
}