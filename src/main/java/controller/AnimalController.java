package controller;

import dto.AnimalCreateDto;
import dto.AnimalResponseDto;
import dto.FarmDto;
import entity.Animal;
import entity.Farm;
import exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import repository.AnimalRepository;
import repository.FarmRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        animal.setWeight(animalDto.getWeight());
        animal.setSpecies(animalDto.getSpecies());
        animal.setFarm(farm);
        animal.setArrivalTime(LocalDateTime.now());

        Animal savedAnimal = animalRepository.save(animal);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(savedAnimal.getId())
                        .toUri())
                .body(convertToDto(savedAnimal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> getAnimal(@PathVariable Integer id) {
        return animalRepository.findById(id)
                .map(animal -> ResponseEntity.ok(convertToDto(animal)))
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));
    }

    @GetMapping("/date/{date}")
    public List<AnimalResponseDto> getAnimalsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return animalRepository.findByArrivalDate(startOfDay, endOfDay)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/farm/{farmId}")
    public List<AnimalResponseDto> getAnimalsByFarm(@PathVariable Integer farmId) {
        return animalRepository.findByFarmId(farmId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/species/{species}")
    public List<AnimalResponseDto> getAnimalsBySpecies(@PathVariable String species) {
        return animalRepository.findBySpecies(species)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AnimalResponseDto convertToDto(Animal animal) {
        AnimalResponseDto dto = new AnimalResponseDto();
        dto.setId(animal.getId());
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
