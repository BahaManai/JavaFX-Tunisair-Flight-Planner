package com.beginsecure.tunisairaeroplan.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String cin;
    private String matricule;
    private LocalDate dateNaissance;
    private String nationalite;
    private String departement;
    private String poste;
    private String baseAffectation;
    private String aeroport;
    private String email;
    private String telephone;
    private String encryptedPassword;
    private String salt;
    private boolean isApproved;
    private boolean isAdmin;
    private LocalDateTime dateInscription;

    public User() {}

    public User(String nom, String prenom, String cin, String matricule, LocalDate dateNaissance,
                String nationalite, String departement, String poste, String baseAffectation,
                String aeroport, String email, String telephone, String encryptedPassword,
                String salt) {
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.matricule = matricule;
        this.dateNaissance = dateNaissance;
        this.nationalite = nationalite;
        this.departement = departement;
        this.poste = poste;
        this.baseAffectation = baseAffectation;
        this.aeroport = aeroport;
        this.email = email;
        this.telephone = telephone;
        this.encryptedPassword = encryptedPassword;
        this.salt = salt;
        this.isApproved = false;
        this.isAdmin = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public void setBaseAffectation(String baseAffectation) {
        this.baseAffectation = baseAffectation;
    }

    public void setAeroport(String aeroport) {
        this.aeroport = aeroport;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getCin() {
        return cin;
    }

    public String getMatricule() {
        return matricule;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getNationalite() {
        return nationalite;
    }

    public String getDepartement() {
        return departement;
    }

    public String getPoste() {
        return poste;
    }

    public String getBaseAffectation() {
        return baseAffectation;
    }

    public String getAeroport() {
        return aeroport;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }
}