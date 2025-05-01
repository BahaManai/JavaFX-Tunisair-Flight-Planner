package com.beginsecure.tunisairaeroplan.Model;

import java.util.HashSet;
import java.util.Scanner;

public class Equipage {
    private int id;
    private String nomEquipage;
    private HashSet<Membre> membres;

    public Equipage(int id, String nomEquipage) {
        this.id = id;
        this.nomEquipage = nomEquipage;
        this.membres = new HashSet<>();
    }

    public boolean estDisponible() {
        for (Membre membre : membres) {
            if (!membre.isDisponible()) {
                return false;
            }
        }
        return true;
    }

    public void ajouterMembre(Membre m) {
        if (membres.contains(m)) {
            System.out.println("Le membre avec CIN " + m.getCin() + " existe déjà dans l’équipage.");
        } else {
            membres.add(m);
            System.out.println("Membre ajouté : " + m.getInfos());
        }
    }

    public void supprimerMembre(Membre m) {
        if (!membres.contains(m)) {
            System.out.println("Membre non trouvé dans l’équipage.");
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Voulez-vous vraiment supprimer le membre " + m.getInfos() + " ? (oui/non) : ");
            String reponse = scanner.nextLine().trim().toLowerCase();
            if (reponse.equals("oui")) {
                membres.remove(m);
                System.out.println("Membre supprimé.");
            } else {
                System.out.println("Suppression annulée.");
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getNomEquipage() {
        return nomEquipage;
    }

    public HashSet<Membre> getMembres() {
        return membres;
    }

    @Override
    public String toString() {
        return "Equipage " + nomEquipage + " (membres: " + membres.size() + ")";
    }
}

