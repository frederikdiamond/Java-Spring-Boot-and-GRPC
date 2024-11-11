package com.example.animalproductservice.controller;

import com.example.animalproductservice.dto.AnimalCreateDto;
import com.example.animalproductservice.dto.AnimalResponseDto;
import com.example.animalproductservice.dto.FarmDto;
import com.example.animalproductservice.entity.Animal;
import com.example.animalproductservice.entity.Farm;
import com.example.animalproductservice.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.animalproductservice.repository.AnimalRepository;
import com.example.animalproductservice.repository.FarmRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {
    private final AnimalRepository animalRepository;
    private final FarmRepository farmRepository;

    public AnimalController(AnimalRepository animalRepository, FarmRepository farmRepository) {
        this.animalRepository = animalRepository;
        this.farmRepository = farmRepository;
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDto> registerAnimal(@Valid @RequestBody AnimalCreateDto animalDto) {
        Farm farm = farmRepository.findById(animalDto.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));

        Animal animal = new Animal();
        animal.setRegistrationNumber(animalDto.getRegistrationNumber());
        animal.setWeight(animalDto.getWeight());
        animal.setSpecies(animalDto.getSpecies());
        animal.setFarm(farm);
        animal.setArrivalTime(LocalDateTime.now());

        Animal savedAnimal = animalRepository.save(animal);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{registrationNumber}")
                        .buildAndExpand(savedAnimal.getRegistrationNumber())
                        .toUri())
                .body(convertToDto(savedAnimal));
    }

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<AnimalResponseDto> getAnimal(@PathVariable("registrationNumber") String registrationNumber) {
        return animalRepository.findByRegistrationNumber(registrationNumber)
                .map(animal -> ResponseEntity.ok(convertToDto(animal)))
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with registration number: " + registrationNumber));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalsByDate(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<Animal> animals = animalRepository.findByArrivalDate(date);

            List<AnimalResponseDto> dtos = animals.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching animals for date: " + date, e);
        }
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalsByFarm(@PathVariable("farmId") Integer farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Farm not found with id: " + farmId);
        }

        List<Animal> animals = animalRepository.findByFarmId(farmId);
        List<AnimalResponseDto> dtos = animals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/species/{species}")
    public ResponseEntity<List<AnimalResponseDto>> getAnimalsBySpecies(@PathVariable("species") String species) {
        try {
            List<Animal> animals = animalRepository.findBySpecies(species);
            List<AnimalResponseDto> dtos = animals.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            System.err.println("Error finding animals by species: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private AnimalResponseDto convertToDto(Animal animal) {
        AnimalResponseDto dto = new AnimalResponseDto();
        dto.setRegistrationNumber(animal.getRegistrationNumber());
        dto.setWeight(animal.getWeight());
        dto.setArrivalTime(animal.getArrivalTime());
        dto.setSpecies(animal.getSpecies());

        if (animal.getFarm() != null) {
            FarmDto farmDto = new FarmDto();
            farmDto.setId(animal.getFarm().getId());
            farmDto.setName(animal.getFarm().getName());
            farmDto.setLocation(animal.getFarm().getLocation());
            dto.setFarm(farmDto);
        }

        return dto;
    }
}
