package com.estock.stock.Dto;


import lombok.Data;

@Data
public class ProduitVenduDTO {
    private String nom;
    private int quantite;
    private double prixUnitaire;
    private double remise;
    private double sousTotal;


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getRemise() {
        return remise;
    }

    public double getSousTotal() {
        return sousTotal;
    }

    public void setSousTotal(double sousTotal) {
        this.sousTotal = sousTotal;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}
