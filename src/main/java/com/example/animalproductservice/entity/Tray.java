package com.example.animalproductservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trays")
public class Tray {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "max_capacity", nullable = false)
    private BigDecimal maxCapacity;

    @Column(name = "current_weight", nullable = false)
    private BigDecimal currentWeight;

    @Column(name = "part_type", nullable = false)
    private String partType;

    @ManyToMany
    @JoinTable(
            name = "tray_parts",
            joinColumns = @JoinColumn(name = "tray_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private Set<Part> parts = new HashSet<>();

    @ManyToMany(mappedBy = "trays")
    private Set<Station> stations = new HashSet<>();
}
