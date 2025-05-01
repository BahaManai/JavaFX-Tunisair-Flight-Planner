package com.beginsecure.tunisairaeroplan.dao;

import com.beginsecure.tunisairaeroplan.Model.Equipage;
import com.beginsecure.tunisairaeroplan.Model.Membre;
import com.beginsecure.tunisairaeroplan.dao.equipageDao;
import com.beginsecure.tunisairaeroplan.dao.membreDao;
import com.beginsecure.tunisairaeroplan.utilites.LaConnexion;

import java.util.ArrayList;
import java.util.HashSet;

public class testDaoMembreEquipage {

    public static void main(String[] args) {
        LaConnexion.seConnecter();

        System.out.println("------ Test MembreDAO ------");

        Membre m1 = new Membre(0, "CIN003", "Ahmed", "Kacem", "Pilote", true);
        Membre m2 = new Membre(0, "CIN004", "Leila", "Ben Salem", "Hôtesse", true);

        // Ajouter les membres
        membreDao.ajouter(m1);
        membreDao.ajouter(m2);

        // Lister les membres
        ArrayList<Membre> listeMembres = membreDao.lister();
        System.out.println("📋 Membres :");
        for (Membre m : listeMembres) {
            System.out.println(" - " + m.getInfos());
        }

        // Modifier un membre
        m1.setNom("Mohamed");
        membreDao.modifier(m1);

        // Supprimer un membre (exemple désactivé ici)
        // membreDao.supprimer(m2);

        System.out.println("------ Test EquipageDAO ------");

        // Créer un équipage avec des membres
        Equipage eq1 = new Equipage(0, "Alpha Team");
        eq1.ajouterMembre(m1);
        eq1.ajouterMembre(m2);

        // Ajouter l’équipage à la base
        equipageDao.ajouter(eq1);

        // Lister tous les équipages
        ArrayList<Equipage> tousEquipages = equipageDao.lister();
        for (Equipage e : tousEquipages) {
            System.out.println("🛫 Équipage : " + e.getNomEquipage());
            for (Membre m : e.getMembres()) {
                System.out.println("   - " + m.getInfos());
            }
        }

        // Modifier l'équipage
        if (!tousEquipages.isEmpty()) {
            Equipage premier = tousEquipages.get(0);
            premier = new Equipage(premier.getId(), "Bravo Team");
            equipageDao.modifier(premier);
        }

        // Supprimer un équipage (exemple désactivé ici)
        // equipageDao.supprimer(eq1);

        System.out.println("✅ Tests terminés.");
    }
}

