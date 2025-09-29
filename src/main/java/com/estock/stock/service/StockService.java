package com.estock.stock.service;
import com.estock.stock.Entity.Produit;
import com.estock.stock.Entity.Utilisateur;
import com.estock.stock.repository.ProduitRepository;
import com.estock.stock.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public String getRoleByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur != null && utilisateur.getRole() != null) {
            return utilisateur.getRole().name();
        }
        return null;
    }
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }
    public Utilisateur updateUtilisateur(Long id, Utilisateur updatedData) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l’ID: " + id));
        utilisateur.setEmail(updatedData.getEmail());
        utilisateur.setPassword(updatedData.getPassword());
        utilisateur.setRole(updatedData.getRole());
        return utilisateurRepository.save(utilisateur);
    }
    public void deleteUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l’ID: " + id);
        }
        utilisateurRepository.deleteById(id);
    }
    public List<Utilisateur> getAll() {
        return utilisateurRepository.findAll();
    }
    public Utilisateur getUtilisateurConnecte() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String email = authentication.getName();
        return utilisateurRepository.findByEmail(email);
    }

    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Stock
    @Autowired
    private ProduitRepository produitRepository;

    public Produit ajouterProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduitById(Long id) {
        return produitRepository.findById(id);
    }

    public Produit updateProduit(Long id, Produit produitDetails) {
        return produitRepository.findById(id)
                .map(produit -> {
                    produit.setNom(produitDetails.getNom());
                    produit.setRef(produitDetails.getRef());
                    produit.setPrix(produitDetails.getPrix());
                    produit.setQte(produitDetails.getQte());
                    return produitRepository.save(produit);
                }).orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'id : " + id));
    }

    public void deleteProduit(Long id) {
        produitRepository.deleteById(id);
    }

    public List<Produit> getProduitsByNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }

    public Optional<Produit> getProduitByRef(String ref) {
        return produitRepository.findByRef(ref);
    }


    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Produit


}
