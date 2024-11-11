package com.example.animalproductservice.entity;

import com.via.pro3.animalproductservice.AnimalProductServiceOuterClass;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "parts")
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private String type;

    @ManyToMany(mappedBy = "parts")
    private Set<Animal> animals = new HashSet<>();

    @ManyToMany(mappedBy = "parts")
    private Set<Product> products = new HashSet<>();

    @ManyToMany(mappedBy = "parts")
    private Set<Tray> trays = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
