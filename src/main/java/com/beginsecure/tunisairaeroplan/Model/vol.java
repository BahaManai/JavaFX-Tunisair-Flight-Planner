package com.beginsecure.tunisairaeroplan.Model;

import com.beginsecure.tunisairaeroplan.Model.enums.StatutVol;
import com.beginsecure.tunisairaeroplan.Model.enums.TypeTrajet;

import java.util.Date;

public class vol {
    private int idVol;
    private String numeroVol;
    private String destination;
    private Date heureDepart;
    private Date heureArrivee;
    private TypeTrajet typeTrajet;
    private StatutVol statut;
    private Avion avion;
    private Equipage equipage;

    public vol() {
    }

    public vol(String numeroVol, String destination, Date heureDepart, Date heureArrivee, TypeTrajet typeTrajet, StatutVol statut, Avion avion, Equipage equipage) {
        this.numeroVol = numeroVol;
        this.destination = destination;
        this.heureDepart = heureDepart;
        this.heureArrivee = heureArrivee;
        this.typeTrajet = typeTrajet;
        this.statut = statut;
        this.avion = avion;
        this.equipage = equipage;
    }

    public void modifierHoraire(Date nvDep, Date nvArr) {
        this.heureDepart = nvDep;
        this.heureArrivee = nvArr;
    }

    public long calculDuree() {
        long diff = heureArrivee.getTime() - heureDepart.getTime();
        return diff / (60 * 60 * 1000); // Conversion du temps en heures
    }

    public void assignerEquipage(Equipage e) {
        this.equipage = e;
    }

    public void assignerAvion(Avion a) {
        this.avion = a;
    }

    public void annuler() {
        this.statut = StatutVol.Annulé;
    }

    public void terminer() {
        this.statut = StatutVol.Terminé;
    }

    public int getIdVol() {
        return idVol;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public String getNumVol() {
        return numeroVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(Date heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Date getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(Date heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public TypeTrajet getTypeTrajet() {
        return typeTrajet;
    }

    public void setTypeTrajet(TypeTrajet typeTrajet) {
        this.typeTrajet = typeTrajet;
    }

    public StatutVol getStatut() {
        return statut;
    }

    public void setStatut(StatutVol statut) {
        this.statut = statut;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public Equipage getEquipage() {
        return equipage;
    }

    public void setEquipage(Equipage equipage) {
        this.equipage = equipage;
    }
}
