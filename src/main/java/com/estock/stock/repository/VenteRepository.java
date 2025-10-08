package com.estock.stock.repository;

import com.estock.stock.Entity.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    @Query("SELECT COUNT(v) FROM Vente v WHERE DATE(v.dateVente) = CURRENT_DATE")
    long countVentesAujourdhui();

    List<Vente> findTop3ByVendeurEmailOrderByDateVenteDesc(String emailVendeur);

    @Query("SELECT v FROM Vente v WHERE DATE(v.dateVente) = CURRENT_DATE")
    List<Vente> findVentesAujourdhui();

}
