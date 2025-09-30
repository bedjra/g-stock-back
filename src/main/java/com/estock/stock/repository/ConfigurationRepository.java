package com.estock.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.module.Configuration;
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

}