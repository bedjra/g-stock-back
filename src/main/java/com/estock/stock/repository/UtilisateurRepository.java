package com.estock.stock.repository;

import com.estock.stock.Entity.Utilisateur;
import com.estock.stock.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Utilisateur findByEmail(String email);
//    Utilisateur findByEmailAndPasswordAndRole(String email, String password, Role role);

    Optional<Utilisateur> findByEmailAndPasswordAndRole(String email, String password, Role role);

    Utilisateur findByEmailAndPassword(String email, String password);



}

