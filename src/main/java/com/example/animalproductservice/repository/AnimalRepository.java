package com.example.animalproductservice.repository;

import com.example.animalproductservice.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    @Query("SELECT DISTINCT a FROM Animal a LEFT JOIN FETCH a.farm WHERE a.species = :species")
    List<Animal> findBySpecies(@Param("species") String species);

    List<Animal> findByFarmId (@Param("farmId") Integer farmId);

    Optional<Animal> findByRegistrationNumber(String registrationNumber);

    @Query("SELECT DISTINCT a FROM Animal a LEFT JOIN FETCH a.farm WHERE CAST(a.arrivalTime AS date) = :date")
    List<Animal> findByArrivalDate(@Param("date") LocalDate date);

    List<Animal> findByWeightBetween(@Param("minWeight") Double minWeight, @Param("maxWeight") Double maxWeight);

    List<Animal> findByFarmIdAndSpecies(@Param("farmId") Integer farmId, @Param("species") String species);

    List<Animal> findAllByOrderByArrivalTimeDesc();

    List<Animal> findByWeightGreaterThan(@Param("weight") Double weight);

    @Query("SELECT DISTINCT a FROM Animal a LEFT JOIN FETCH a.parts WHERE a.id = :animalId")
    Animal findAnimalWithParts(@Param("animalId") Integer animalId);
}
