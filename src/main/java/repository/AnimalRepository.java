package repository;

import entity.Animal;
import entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    List<Animal> findBySpecies(String species);

    List<Animal> findByFarmId (Integer farmId);

    @Query("SELECT a FROM Animal a WHERE a.arrivalTime BETWEEN :startOfDay AND :endOfDay")
    List<Animal> findByArrivalDate(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    List<Animal> findByWeightBetween(Double minWeight, Double maxWeight);

    List<Animal> findByFarmIdAndSpecies(Integer farmId, String species);

    List<Animal> findAllByOrderByArrivalTimeDesc();

    List<Animal> findByWeightGreaterThan(Double weight);

    @Query("SELECT DISTINCT a FROM Animal a LEFT JOIN FETCH a.parts WHERE a.id = :animalId")
    Animal findAnimalWithParts(@Param("animalId") Integer animalId);
}
