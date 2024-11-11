package com.example.animalproductservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal weight;

    @Column(nullable = false)
    private boolean recalled;

    @ManyToMany
    @JoinTable(
            name = "product_parts",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    private Set<Part> parts = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    private Set<RetailStore> stores = new HashSet<>();
}
