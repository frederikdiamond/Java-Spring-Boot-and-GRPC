package com.example.animalproductservice;

import com.example.animalproductservice.dto.AnimalCreateDto;
import com.example.animalproductservice.entity.Animal;
import com.example.animalproductservice.entity.Farm;
import com.example.animalproductservice.repository.AnimalRepository;
import com.example.animalproductservice.repository.FarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AnimalProductServiceApplication.class)
@AutoConfigureMockMvc
public class AnimalRegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AnimalRepository animalRepository;

    @MockBean
    private FarmRepository farmRepository;

    @Test
    public void testRegisterAnimal_Success() throws Exception {
        AnimalCreateDto createDto = new AnimalCreateDto();
        createDto.setRegistrationNumber("AN001");
        createDto.setWeight(new BigDecimal("1200.50"));
        createDto.setSpecies("Cow");
        createDto.setFarmId(1);

        Farm farm = new Farm();
        farm.setId(1);
        farm.setName("Green Valley Farm");
        farm.setLocation("North Field");

        Animal savedAnimal = new Animal();
        savedAnimal.setId(1);
        savedAnimal.setRegistrationNumber("AN001");
        savedAnimal.setWeight(new BigDecimal("1200.50"));
        savedAnimal.setSpecies("Cow");
        savedAnimal.setFarm(farm);
        savedAnimal.setArrivalTime(LocalDateTime.now());

        when(farmRepository.findById(1)).thenReturn(Optional.of(farm));
        when(animalRepository.save(any(Animal.class))).thenReturn(savedAnimal);

        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationNumber").value("AN001"))
                .andExpect(jsonPath("$.species").value("Cow"))
                .andExpect(jsonPath("$.weight").value("1200.5"))
                .andExpect(jsonPath("$.farm.id").value(1));
    }

    @Test
    public void testRegisterAnimal_FarmNotFound() throws Exception {
        AnimalCreateDto createDto = new AnimalCreateDto();
        createDto.setRegistrationNumber("AN001");
        createDto.setWeight(new BigDecimal("1200.50"));
        createDto.setSpecies("Cow");
        createDto.setFarmId(999);

        when(farmRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAnimal_Success() throws Exception {
        Farm farm = new Farm();
        farm.setId(1);
        farm.setName("Green Valley Farm");
        farm.setLocation("North Field");

        Animal animal = new Animal();
        animal.setId(1);
        animal.setRegistrationNumber("AN001");
        animal.setWeight(new BigDecimal("1200.50"));
        animal.setSpecies("Cow");
        animal.setFarm(farm);
        animal.setArrivalTime(LocalDateTime.now());

        when(animalRepository.findByRegistrationNumber("AN001")).thenReturn(Optional.of(animal));

        mockMvc.perform(get("/api/animals/AN001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber").value("AN001"))
                .andExpect(jsonPath("$.species").value("Cow"))
                .andExpect(jsonPath("$.weight").value("1200.5"))
                .andExpect(jsonPath("$.farm.id").value(1));
    }

    @Test
    public void testGetAnimalsByDate_Success() throws Exception {
        LocalDate testDate = LocalDate.now();
        List<Animal> animals = Arrays.asList(
                createTestAnimal("AN001", "Cow", 1),
                createTestAnimal("AN002", "Pig", 1)
        );

        when(animalRepository.findByArrivalDate(testDate)).thenReturn(animals);

        mockMvc.perform(get("/api/animals/date/{date}", testDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].registrationNumber").value("AN001"))
                .andExpect(jsonPath("$[1].registrationNumber").value("AN002"));
    }

    @Test
    public void testGetAnimalsByFarm_Success() throws Exception {
        List<Animal> animals = Arrays.asList(
                createTestAnimal("AN001", "Cow", 1),
                createTestAnimal("AN002", "Pig", 1)
        );

        when(farmRepository.existsById(1)).thenReturn(true);
        when(animalRepository.findByFarmId(1)).thenReturn(animals);

        mockMvc.perform(get("/api/animals/farm/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].registrationNumber").value("AN001"))
                .andExpect(jsonPath("$[1].registrationNumber").value("AN002"));
    }

    @Test
    public void testGetAnimalsBySpecies_Success() throws Exception {
        List<Animal> animals = Arrays.asList(
                createTestAnimal("AN001", "Cow", 1),
                createTestAnimal("AN003", "Cow", 2)
        );

        when(animalRepository.findBySpecies("Cow")).thenReturn(animals);

        mockMvc.perform(get("/api/animals/species/Cow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].species").value("Cow"))
                .andExpect(jsonPath("$[1].species").value("Cow"));
    }

    private Animal createTestAnimal(String regNumber, String species, int farmId) {
        Farm farm = new Farm();
        farm.setId(farmId);
        farm.setName("Farm " + farmId);
        farm.setLocation("Location " + farmId);

        Animal animal = new Animal();
        animal.setRegistrationNumber(regNumber);
        animal.setWeight(new BigDecimal("1200.50"));
        animal.setSpecies(species);
        animal.setFarm(farm);
        animal.setArrivalTime(LocalDateTime.now());
        return animal;
    }
}
