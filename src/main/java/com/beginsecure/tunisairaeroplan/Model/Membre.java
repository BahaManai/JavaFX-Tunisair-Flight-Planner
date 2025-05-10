package com.beginsecure.tunisairaeroplan.Model;

import java.util.Objects;

public class Membre {
    private int id;
    private String cin;
    private String nom;
    private String prenom;
    private String role;
    private boolean estDisponible;

    public Membre(int id, String cin, String nom, String prenom, String role, boolean estDisponible) {
        this.id = id;
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.estDisponible = estDisponible;
    }

    public String getInfos() {
        return nom + " " + prenom + " (" + role + ")";
    }

    public boolean isDisponible() {
        return estDisponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Membre membre)) return false;
        return Objects.equals(cin, membre.cin);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + role + ")";
    }

}
