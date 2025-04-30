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
public long calculDuree(){
        long res=heureArrivee.getTime()- heureDepart.getTime();
        return res/(60*1000);
}

    public void assignerEquipage(Equipage e) {
        this.equipage = e;
    }

    public void assignerAvion(Avion a) {
        this.avion = a;
    }

    public vol() {
    }

    public int getIdVol() {
        return idVol;
    }

    public String getNumeroVol() {
        return numeroVol;
    }

    public String getDestination() {
        return destination;
    }

    public Date getHeureDepart() {
        return heureDepart;
    }

    public Date getHeureArrivee() {
        return heureArrivee;
    }

    public TypeTrajet getTypeTrajet() {
        return typeTrajet;
    }

    public StatutVol getStatut() {
        return statut;
    }

    public Avion getAvion() {
        return avion;
    }

    public Equipage getEquipage() {
        return equipage;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setHeureDepart(Date heureDepart) {
        this.heureDepart = heureDepart;
    }

    public void setHeureArrivee(Date heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public void setTypeTrajet(TypeTrajet typeTrajet) {
        this.typeTrajet = typeTrajet;
    }

    public void setStatut(StatutVol statut) {
        this.statut = statut;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public void setEquipage(Equipage equipage) {
        this.equipage = equipage;
    }

    public void annuler() {
        this.statut = StatutVol.Annulé;
    }

    public void terminer() {
        this.statut = StatutVol.Terminé;
    }
}
