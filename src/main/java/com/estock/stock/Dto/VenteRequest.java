package com.estock.stock.Dto;

import com.estock.stock.Entity.Vente;
import lombok.Data;

@Data
public class VenteRequest {
    private Vente vente;
    private String emailVendeur;


    public Vente getVente() {
        return vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public String getEmailVendeur() {
        return emailVendeur;
    }

    public void setEmailVendeur(String emailVendeur) {
        this.emailVendeur = emailVendeur;
    }
}
