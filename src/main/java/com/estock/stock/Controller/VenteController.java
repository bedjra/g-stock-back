package com.estock.stock.Controller;

import com.estock.stock.Dto.VenteResponseDTO;
import com.estock.stock.Entity.LigneVente;
import com.estock.stock.Entity.Produit;
import com.estock.stock.Entity.Utilisateur;
import com.estock.stock.Entity.Vente;
import com.estock.stock.service.Facturepdf;
import com.estock.stock.service.StockService;
import com.estock.stock.service.VenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vente")
@CrossOrigin(origins = "http://localhost:4200")

public class VenteController {

    private final VenteService venteService;

    public VenteController(VenteService venteService) {
        this.venteService = venteService;
    }

    @Autowired
    private StockService stockService;

    @Autowired
    private Facturepdf facturepdf;

//    @PostMapping
//    public ResponseEntity<?> enregistrerVente(@RequestBody Map<String, Object> payload) {
//        try {
//            String emailVendeur = (String) payload.get("emailVendeur");
//
//            if (emailVendeur == null || emailVendeur.isEmpty()) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("error", "Email du vendeur requis"));
//            }
//
//            Vente vente = new Vente();
//            List<Map<String, Object>> lignesData = (List<Map<String, Object>>) payload.get("lignes");
//            List<LigneVente> lignes = lignesData.stream().map(ligneData -> {
//                LigneVente ligne = new LigneVente();
//                ligne.setQuantite((Integer) ligneData.get("quantite"));
//
//                Map<String, Object> produitData = (Map<String, Object>) ligneData.get("produit");
//                Produit produit = new Produit();
//                produit.setId(((Number) produitData.get("id")).longValue());
//                ligne.setProduit(produit);
//                ligne.setVente(vente);
//
//                return ligne;
//            }).collect(Collectors.toList());
//
//            vente.setLignes(lignes);
//
//            // Enregistrer la vente
//            VenteResponseDTO response = venteService.enregistrerVente(vente, emailVendeur);
//
//            // 🔥 Générer le PDF
//            byte[] pdfBytes = facturepdf.genererFacturePdf(response);
//
//            // Retourner le PDF avec les bons headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_PDF);
//            headers.setContentDisposition(
//                    ContentDisposition.builder("attachment")
//                            .filename("facture_" + response.getId() + ".pdf")
//                            .build()
//            );
//
//            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }
//


    @PostMapping
    public ResponseEntity<?> enregistrerVente(@RequestBody Map<String, Object> payload) {
        try {
            String emailVendeur = (String) payload.get("emailVendeur");
            if (emailVendeur == null || emailVendeur.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email du vendeur requis"));
            }

            Vente vente = new Vente();

            List<Map<String, Object>> lignesData = (List<Map<String, Object>>) payload.get("lignes");
            if (lignesData == null || lignesData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucune ligne de vente fournie"));
            }

            List<LigneVente> lignes = lignesData.stream().map(ligneData -> {
                LigneVente ligne = new LigneVente();
                ligne.setQuantite(((Number) ligneData.get("quantite")).intValue());

                // ✅ Gestion de la remise spécifique
                if (ligneData.containsKey("remise") && ligneData.get("remise") != null) {
                    ligne.setRemise(((Number) ligneData.get("remise")).doubleValue());
                } else {
                    ligne.setRemise(0);
                }

                Map<String, Object> produitData = (Map<String, Object>) ligneData.get("produit");
                Produit produit = new Produit();
                produit.setId(((Number) produitData.get("id")).longValue());
                ligne.setProduit(produit);
                ligne.setVente(vente);
                return ligne;
            }).collect(Collectors.toList());

            vente.setLignes(lignes);

            // 4️⃣ Enregistrer la vente
            VenteResponseDTO response = venteService.enregistrerVente(vente, emailVendeur);

            // 🔥 Générer le PDF
            byte[] pdfBytes = facturepdf.genererFacturePdf(response);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("facture_" + response.getId() + ".pdf")
                    .build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/count")
    public ResponseEntity<Long> getNombreVentesAujourdhui() {
        long count = venteService.getNombreVentesAujourdhui();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/montant")
    public ResponseEntity<Double> getMontantTotalVentesAujourdhui() {
        double total = venteService.getMontantTotalVentesAujourdhui();
        return ResponseEntity.ok(total);
    }


    @GetMapping
    public List<Vente> getAllVentes() {
        return venteService.getAllVentes();
    }


    @GetMapping("/recentes")
    public ResponseEntity<List<Map<String, Object>>> getTroisDernieresVentes() {
        List<Map<String, Object>> ventes = venteService.getTroisDernieresVentes();
        return ResponseEntity.ok(ventes);
    }



}
