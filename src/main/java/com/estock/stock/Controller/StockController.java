package com.estock.stock.Controller;

import com.estock.stock.Entity.Produit;
import com.estock.stock.service.StockService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")

public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping
    public ResponseEntity<Produit> ajouterProduit(@RequestBody Produit produit) {
        Produit nouveauProduit = stockService.ajouterProduit(produit);
        return ResponseEntity.ok(nouveauProduit);
    }
    @GetMapping
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(stockService.getAllProduits());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return stockService.getProduitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable Long id, @RequestBody Produit produit) {
        try {
            return ResponseEntity.ok(stockService.updateProduit(id, produit));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        stockService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
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






//    @Autowired
//    private com.eduMap.edumap.GLOBALE.service.stockService utilisateurService;
//
//    @Autowired
//    private ConfigurationService configurationService;
//
//
//    // Obtenir tous les rôles
//    @Operation(summary = "Récupérer tous les roles")
//    @GetMapping("/roles")
//    public ResponseEntity<Role[]> getAllRoles() {
//        return ResponseEntity.ok(Role.values());
//    }
//
//
//    // Obtenir tous les utilisateurs
//    @Operation(summary = "Récupérer tous les utilisateurs")
//    @GetMapping("/utilisateur")
//    public ResponseEntity<List<Utilisateur>> getAllUtilisateur() {
//        List<Utilisateur> utilisateurs = utilisateurService.getAll();
//        return ResponseEntity.ok(utilisateurs);
//    }
//
//
//    // Connexion
//    @Operation(summary = "Connexion")
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
//        try {
//            String email = credentials.get("email");
//            String password = credentials.get("password");
//
//            Map<String, Object> response = utilisateurService.login(email, password);
//            return ResponseEntity.ok(response);
//
//        } catch (IllegalStateException e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
//
//        } catch (IllegalArgumentException e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
//
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", "Erreur serveur inattendue");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    @Operation(summary = "Inscription d'un compte")
//    @PostMapping("/save")
//    public ResponseEntity<Utilisateur> saveUtilisateur(@RequestBody Utilisateur utilisateur) {
//        Utilisateur savedUser = utilisateurService.saveUtilisateur(utilisateur);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
//    }
//
//
//
//    @Operation(summary = "modifier un compte")
//    @PutMapping("/{id}")
//    public ResponseEntity<Utilisateur> updateUtilisateur(
//            @PathVariable Long id,
//            @RequestBody Utilisateur updatedData) {
//        Utilisateur utilisateur = utilisateurService.updateUtilisateur(id, updatedData);
//        return ResponseEntity.ok(utilisateur);
//    }
//
//    @Operation(summary = "delete un compte")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
//        utilisateurService.deleteUtilisateur(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @Operation(summary = "Récupérer le role qui est connecté")
//    @GetMapping("/role/{email}")
//    public ResponseEntity<String> getRoleByEmail(@PathVariable String email) {
//        String role = utilisateurService.getRoleByEmail(email);
//        if (role != null) {
//            return ResponseEntity.ok(role);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rôle introuvable");
//        }
//    }
//
//
//    @Operation(summary = "Obtenir l'utilisateur connecté")
//    @GetMapping("/info")
//    public ResponseEntity<?> getUtilisateurConnecte() {
//        Utilisateur utilisateur = utilisateurService.getUtilisateurConnecte();
//        if (utilisateur != null) {
//            return ResponseEntity.ok(utilisateur);
//        } else {
//            return ResponseEntity.status(401).body("Aucun utilisateur connecté");
//        }
//    }




}
