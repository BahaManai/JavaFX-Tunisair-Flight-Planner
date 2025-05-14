package com.beginsecure.tunisairaeroplan.Model;

import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;

public class Avion {
    private int id;
    private String marque;
    private String modele;
    private int capacite;
    private boolean estDisponible;
    private TypeTrajet typeTrajet;

    public Avion() {}

    public Avion(String marque, String modele, int capacite, boolean estDisponible, TypeTrajet typeTrajet) {
        this.marque = marque;
        this.modele = modele;
        this.capacite = capacite;
        this.estDisponible = estDisponible;
        this.typeTrajet = typeTrajet;
    }

    // Getters et Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public boolean isEstDisponible() { return estDisponible; }
    public void setEstDisponible(boolean estDisponible) { this.estDisponible = estDisponible; }

    public TypeTrajet getTypeTrajet() { return typeTrajet; }
    public void setTypeTrajet(TypeTrajet typeTrajet) { this.typeTrajet = typeTrajet; }

    @Override
    public String toString() {
        return marque + " "+ modele + " (" + capacite + " places)";
    }
}
