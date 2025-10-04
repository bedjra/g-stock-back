package com.estock.stock.service;
import com.estock.stock.Entity.*;
import com.estock.stock.repository.ConfigurationRepository;
import com.estock.stock.repository.ProduitRepository;
import com.estock.stock.repository.ReapproRepository;
import com.estock.stock.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ReapproRepository reapproRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;





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
    public Produit ajouterProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduitByNom(String nom) {
        return produitRepository.findByNom(nom);
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

    public List<Produit> importProduitsFromExcel(MultipartFile file) throws Exception {
        List<Produit> produits = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0); // On prend la première feuille
            Iterator<Row> rows = sheet.iterator();

            boolean firstRow = true;
            while (rows.hasNext()) {
                Row row = rows.next();

                // Ignorer la première ligne si c'est l'entête
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                String nom = null;
                String ref = null;
                Integer prix = null;
                Integer qte = 0;

                Cell cellNom = row.getCell(0);
                if (cellNom != null) nom = cellNom.getStringCellValue();

                Cell cellRef = row.getCell(1);
                if (cellRef != null) ref = cellRef.getStringCellValue();

                Cell cellPrix = row.getCell(2);
                if (cellPrix != null) prix = (int) cellPrix.getNumericCellValue();

                Cell cellQte = row.getCell(3);
                if (cellQte != null) qte = (int) cellQte.getNumericCellValue();

                // Vérifier les champs obligatoires
                if (nom == null || prix == null) {
                    // On peut aussi logguer un warning ici
                    continue;
                }

                Produit produit = new Produit();
                produit.setNom(nom);
                produit.setRef(ref);
                produit.setPrix(prix);
                produit.setQte(qte);

                produits.add(produit);
            }

            // Sauvegarde en base
            produitRepository.saveAll(produits);
        }

        return produits;
    }

    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Reappro
    @Transactional
    public Reappro enregistrerReappro(Reappro reappro) {
        List<ReapproProduit> produits = reappro.getProduits();

        for (ReapproProduit rp : produits) {
            Produit produit = produitRepository.findById(rp.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            // Mise à jour du stock
            produit.setQte(produit.getQte() + rp.getQte());
            produitRepository.save(produit);

            // Lier le produit à l'objet ReapproProduit
            rp.setProduit(produit);
            rp.setReappro(reappro);
        }

        return reapproRepository.save(reappro);
    }


    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Config


    public List<Configuration> getAllConfigurations() {
        return configurationRepository.findAll();
    }

    public byte[] getImage() {
        return configurationRepository.findLogo();
    }






}
