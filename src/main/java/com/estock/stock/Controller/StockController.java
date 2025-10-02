package com.estock.stock.Controller;

import com.estock.stock.Entity.Produit;
import com.estock.stock.Entity.Reappro;
import com.estock.stock.Entity.Utilisateur;
import com.estock.stock.Entity.Configuration;
import com.estock.stock.enums.Role;
import com.estock.stock.repository.ConfigurationRepository;
import com.estock.stock.repository.UtilisateurRepository;
import com.estock.stock.service.PdfService;
import com.estock.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")

public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;


    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody Utilisateur request) {
        // Recherche l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail());

        if (utilisateur == null) {
            return ResponseEntity.status(401).body("Utilisateur non trouvé");
        }

        // Vérification du mot de passe
        if (!utilisateur.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Mot de passe incorrect");
        }

        // Si succès, renvoyer l'utilisateur (id + email + role)
        return ResponseEntity.ok(utilisateur);
    }

    @GetMapping("/user/roles")
    public ResponseEntity<Role[]> getAllRoles() {
        return ResponseEntity.ok(Role.values());
    }

    @Operation(summary = "Récupérer tous les utilisateurs")
    @GetMapping("/user")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateur() {
        List<Utilisateur> utilisateurs = stockService.getAll();
        return ResponseEntity.ok(utilisateurs);
    }
    @Operation(summary = "Inscription d'un compte")
    @PostMapping("/user/save")
    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
        Utilisateur savedUser = stockService.saveUtilisateur(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    @Operation(summary = "modifier un compte")
    @PutMapping("/user/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(
            @PathVariable Long id,
            @RequestBody Utilisateur updatedData) {
        Utilisateur utilisateur = stockService.updateUtilisateur(id, updatedData);
        return ResponseEntity.ok(utilisateur);
    }

    @Operation(summary = "delete un compte")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        stockService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Récupérer le role qui est connecté")
    @GetMapping("/user/role/{email}")
    public ResponseEntity<String> getRoleByEmail(@PathVariable String email) {
        String role = stockService.getRoleByEmail(email);
        if (role != null) {
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rôle introuvable");
        }
    }


    @Operation(summary = "Obtenir l'utilisateur connecté")
    @GetMapping("/user/info")
    public ResponseEntity<?> getUtilisateurConnecte() {
        Utilisateur utilisateur = stockService.getUtilisateurConnecte();
        if (utilisateur != null) {
            return ResponseEntity.ok(utilisateur);
        } else {
            return ResponseEntity.status(401).body("Aucun utilisateur connecté");
        }
    }


    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Stock
    @PostMapping("/stock")
    public ResponseEntity<Produit> ajouterProduit(@RequestBody Produit produit) {
        Produit nouveauProduit = stockService.ajouterProduit(produit);
        return ResponseEntity.ok(nouveauProduit);
    }
    @GetMapping("/stock")
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(stockService.getAllProduits());
    }
    @GetMapping("/stock/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return stockService.getProduitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/stock/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable Long id, @RequestBody Produit produit) {
        try {
            return ResponseEntity.ok(stockService.updateProduit(id, produit));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/stock/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        stockService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/stock/search")
    public ResponseEntity<?> searchProduit(@RequestParam(required = false) String nom,
                                           @RequestParam(required = false) String ref) {
        if (nom != null) {
            List<Produit> produits = stockService.getProduitsByNom(nom);
            if (produits.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(produits);
        } else if (ref != null) {
            return stockService.getProduitByRef(ref)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.badRequest().body("Veuillez fournir soit un nom, soit une référence en paramètre.");
        }
    }
    @PostMapping("/stock/import")
    public ResponseEntity<List<Produit>> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<Produit> produits = stockService.importProduitsFromExcel(file);
            return ResponseEntity.ok(produits);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Config   
//    @GetMapping
//    public Configuration getConfiguration() {
//        Configuration config = configurationRepository.findById(1L).orElseThrow();
//
//        if (config.getLogo() != null) {
//            String base64Logo = Base64.getEncoder().encodeToString(config.getLogo());
//            // Crée un nouveau champ temporaire pour Angular
//            config.setLogo(("data:image/png;base64," + base64Logo).getBytes());
//        }
//
//        return config;
//    }
    @GetMapping
    public Configuration getConfiguration() {
        Configuration config = configurationRepository.findById(1L).orElseThrow();

        if (config.getLogo() != null) {
            String base64Logo = Base64.getEncoder().encodeToString(config.getLogo());
            // Retourner directement la string base64
            config.setLogoBase64(base64Logo); // Ajoutez un champ String logoBase64 dans votre entité
            config.setLogo(null); // Éviter d'envoyer les bytes bruts
        }

        return config;
    }

    // Si tu veux récupérer directement en Base64 (ex : pour Angular)
    @GetMapping("/{id}/logo")
    public ResponseEntity<String> getLogoBase64(@PathVariable Long id) {
        Optional<Configuration> configOpt = configurationRepository.findById(id);

        if (configOpt.isPresent() && configOpt.get().getLogo() != null) {
            String base64Logo = java.util.Base64.getEncoder().encodeToString(configOpt.get().getLogo());
            return ResponseEntity.ok(base64Logo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Reappro
    @PostMapping("/reappro")
    public ResponseEntity<Reappro> ajouterReappro(@RequestBody Reappro reappro) {
        try {
            Reappro reapproEnregistre = stockService.enregistrerReappro(reappro);
            return new ResponseEntity<>(reapproEnregistre, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // // // // // // // // // // // // // // // // // // // //
    // // // // //// // //  Invent
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdf() {
        ByteArrayInputStream bis = pdfService.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=inventaire.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }


}
