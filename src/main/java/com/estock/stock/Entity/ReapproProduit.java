package com.estock.stock.Entity;

import jakarta.persistence.*;

@Entity
public class ReapproProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    private int qte;

    @ManyToOne
    @JoinColumn(name = "reappro_id")
    private Reappro reappro;

    // getters et setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQte() {
        return qte;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public Reappro getReappro() {
        return reappro;
    }

    public void setReappro(Reappro reappro) {
        this.reappro = reappro;
    }
}
