package com.estock.stock.service;

import com.estock.stock.Dto.ProduitVenduDTO;
import com.estock.stock.Dto.VenteResponseDTO;
import com.estock.stock.Entity.*;
import com.estock.stock.repository.LigneVenteRepository;
import com.estock.stock.repository.ProduitRepository;
import com.estock.stock.repository.UtilisateurRepository;
import com.estock.stock.repository.VenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenteService {

    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;

    public VenteService(
            VenteRepository venteRepository,
            LigneVenteRepository ligneVenteRepository,
            ProduitRepository produitRepository,
            UtilisateurRepository utilisateurRepository) {
        this.venteRepository = venteRepository;
        this.ligneVenteRepository = ligneVenteRepository;
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Vente> getAllVentes() {
        return venteRepository.findAll();
    }



    @Transactional
    public VenteResponseDTO enregistrerVente(Vente vente, String emailVendeur) {
        // 1️⃣ Vérifier le vendeur
        Utilisateur vendeur = utilisateurRepository.findByEmail(emailVendeur);
        if (vendeur == null) {
            throw new RuntimeException("Vendeur non trouvé");
        }

        // 2️⃣ Calcul total + mise à jour des stocks
        double total = 0;
        for (LigneVente ligne : vente.getLignes()) {
            Produit produit = produitRepository.findById(ligne.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            produit.setQte(produit.getQte() - ligne.getQuantite());
            produitRepository.save(produit);
            total += ligne.getQuantite() * produit.getPrix();
        }

        // 3️⃣ Sauvegarde de la vente
//        vente.setUtilisateur(vendeur);
        vente.setDateVente(LocalDateTime.now());
        vente.setTotal(total);
        Vente savedVente = venteRepository.save(vente);

        // 4️⃣ Conversion vers DTO
        VenteResponseDTO response = new VenteResponseDTO();
        response.setId(savedVente.getId());
        response.setDateVente(savedVente.getDateVente());
        response.setVendeur(vendeur.getEmail());
        response.setTotal(savedVente.getTotal());
        response.setProduits(savedVente.getLignes().stream().map(ligne -> {
            Produit produit = produitRepository.findById(ligne.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            ProduitVenduDTO p = new ProduitVenduDTO();
            p.setNom(produit.getNom());
            p.setQuantite(ligne.getQuantite());
            p.setPrixUnitaire(produit.getPrix());
            return p;
        }).collect(Collectors.toList()));


        return response;
    }
}
