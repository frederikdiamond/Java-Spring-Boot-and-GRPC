package com.example.animalproductservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AnimalResponseDto {
    private String registrationNumber;
    private BigDecimal weight;
    private LocalDateTime arrivalTime;
    private String species;
    private FarmDto farm;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public FarmDto getFarm() {
        return farm;
    }

    public void setFarm(FarmDto farm) {
        this.farm = farm;
    }
}
