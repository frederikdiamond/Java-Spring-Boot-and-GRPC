package com.example.animalproductservice.controller;

import com.example.animalproductservice.dto.FarmCreateDto;
import com.example.animalproductservice.dto.FarmDto;
import com.example.animalproductservice.entity.Farm;
import com.example.animalproductservice.exceptions.ResourceNotFoundException;
import com.example.animalproductservice.repository.FarmRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmRepository farmRepository;

    public FarmController(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @PostMapping
    public ResponseEntity<FarmDto> createFarm(@Valid @RequestBody FarmCreateDto farmDto) {
        Farm farm = new Farm();
        farm.setName(farmDto.getName());
        farm.setLocation(farmDto.getLocation());

        Farm savedFarm = farmRepository.save(farm);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(savedFarm.getId())
                        .toUri())
                .body(convertToDto(savedFarm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmDto> getFarm(@PathVariable("id") Integer id) {
        return farmRepository.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));
    }

    private FarmDto convertToDto(Farm farm) {
        FarmDto dto = new FarmDto();
        dto.setId(farm.getId());
        dto.setName(farm.getName());
        dto.setLocation(farm.getLocation());
        return dto;
    }
}
