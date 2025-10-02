package com.estock.stock.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Reappro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateArrivage;

    @OneToMany(mappedBy = "reappro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReapproProduit> produits;

    // getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateArrivage() {
        return dateArrivage;
    }

    public void setDateArrivage(LocalDate dateArrivage) {
        this.dateArrivage = dateArrivage;
    }

    public List<ReapproProduit> getProduits() {
        return produits;
    }

    public void setProduits(List<ReapproProduit> produits) {
        this.produits = produits;
    }
}
