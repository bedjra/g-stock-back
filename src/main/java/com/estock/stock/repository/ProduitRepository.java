package com.estock.stock.repository;

import com.estock.stock.Entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    List<Produit> findByNomContainingIgnoreCase(String nom);
    Optional<Produit> findByNom(String nom);

    Optional<Produit> findByRef(String ref);




}
