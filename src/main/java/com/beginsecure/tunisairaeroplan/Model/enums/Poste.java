package com.beginsecure.tunisairaeroplan.Model.enums;
public enum Poste {
    CHEF_DEPARTEMENT("Chef de Département"),
    TECHNICIEN("Technicien"),
    AGENT_COMMERCIAL("Agent Commercial"),
    COMPTABLE("Comptable"),
    INGENIEUR("Ingénieur"),
    AGENT_SECURITE("Agent de Sécurité"),
    ADMINISTRATEUR_RESEAU("Administrateur Réseau");

    private final String displayName;

    Poste(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
}