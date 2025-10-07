package com.estock.stock.repository;

import com.estock.stock.Entity.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    @Query("SELECT COUNT(v) FROM Vente v WHERE DATE(v.dateVente) = CURRENT_DATE")
    long countVentesAujourdhui();

}
