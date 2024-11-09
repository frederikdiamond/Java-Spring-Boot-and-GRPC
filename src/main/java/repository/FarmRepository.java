package repository;

import entity.Animal;
import entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Integer> {
    Optional<Farm> findByName(String name);

    List<Farm> findByLocation(String location);

    List<Farm> findByNameContainingIgnoreCase(String namePart);

    @Query("SELECT DISTINCT f FROM Farm f JOIN f.animals a WHERE a.species = :species")
    List<Farm> findFarmsWithAnimalSpecies(@Param("species") String species);

    @Query("SELECT COUNT(a) FROM Animal a WHERE a.farm.id = :farmId")
    Long countAnimalsByFarmId(@Param("farmId") Integer farmId);
}
