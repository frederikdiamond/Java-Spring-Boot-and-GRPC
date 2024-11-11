package com.example.animalproductservice.repository;

import com.example.animalproductservice.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Integer> {
    Optional<Farm> findByName(@Param("name") String name);

    List<Farm> findByLocation(@Param("location") String location);

    List<Farm> findByNameContainingIgnoreCase(@Param("namePart") String namePart);

    @Query("SELECT DISTINCT f FROM Farm f JOIN f.animals a WHERE a.species = :species")
    List<Farm> findFarmsWithAnimalSpecies(@Param("species") String species);

    @Query("SELECT COUNT(a) FROM Animal a WHERE a.farm.id = :farmId")
    Long countAnimalsByFarmId(@Param("farmId") Integer farmId);

    @Override
    Optional<Farm> findById(@Param("id") Integer id);
}
