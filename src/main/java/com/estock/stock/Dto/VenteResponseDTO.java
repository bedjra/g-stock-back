package com.estock.stock.Dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class VenteResponseDTO {
    private Long id;
    private LocalDateTime dateVente;
    private String vendeur;
    private double total;
    private List<ProduitVenduDTO> produits;

    private double totalHT;
    private double remise;
    private double totalTTC;


    public double getTotalHT() {
        return totalHT;
    }

    public void setTotalHT(double totalHT) {
        this.totalHT = totalHT;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public double getTotalTTC() {
        return totalTTC;
    }

    public void setTotalTTC(double totalTTC) {
        this.totalTTC = totalTTC;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public String getVendeur() {
        return vendeur;
    }

    public void setVendeur(String vendeur) {
        this.vendeur = vendeur;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ProduitVenduDTO> getProduits() {
        return produits;
    }

    public void setProduits(List<ProduitVenduDTO> produits) {
        this.produits = produits;
    }
}
