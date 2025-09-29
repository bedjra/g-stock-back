package com.estock.stock.service;
import com.estock.stock.Entity.Produit;
import com.estock.stock.repository.ProduitRepository;
import com.estock.stock.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

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


}
