package dto;

import java.time.LocalDateTime;

public class AnimalResponseDto {
    private Integer id;
    private Double weight;
    private LocalDateTime arrivalTime;
    private String species;
    private FarmDto farm;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
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
