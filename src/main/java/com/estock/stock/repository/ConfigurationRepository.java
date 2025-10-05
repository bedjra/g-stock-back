package com.estock.stock.repository;


import com.estock.stock.Entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {
    @Query("SELECT c.logo FROM Configuration c WHERE c.id = (SELECT MIN(c2.id) FROM Configuration c2)")
    byte[] findLogo();

    @Query("SELECT c FROM Configuration c")
    Configuration getConfiguration();
}
