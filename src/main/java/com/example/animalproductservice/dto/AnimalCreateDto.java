package com.example.animalproductservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AnimalCreateDto {
    @NotNull(message = "Registration number is required")
    @Pattern(regexp = "AN\\d{3}", message = "Registration number must be in format AN followed by 3 digits")
    private String registrationNumber;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private BigDecimal weight;

    @NotBlank(message = "Species is required")
    private String species;

    @NotNull(message = "Farm ID is required")
    private Integer farmId;

    public @NotNull(message = "Registration number is required") @Pattern(regexp = "AN\\d{3}", message = "Registration number must be in format AN followed by 3 digits") String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(@NotNull(message = "Registration number is required") @Pattern(regexp = "AN\\d{3}", message = "Registration number must be in format AN followed by 3 digits") String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getFarmId() {
        return farmId;
    }

    public void setFarmId(Integer farmId) {
        this.farmId = farmId;
    }
}
