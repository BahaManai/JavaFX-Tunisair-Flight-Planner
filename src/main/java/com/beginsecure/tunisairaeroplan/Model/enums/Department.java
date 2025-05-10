package com.beginsecure.tunisairaeroplan.Model.enums;

public enum Department {
    RESSOURCES_HUMAINES("Ressources Humaines"),
    TECHNICIEN("Technicien"),
    COMMERCIAL("Commercial"),
    FINANCE("Finance"),
    MAINTENANCE("Maintenance"),
    SECURITE("Sécurité"),
    INFORMATIQUE("Informatique");

    private final String displayName;
    Department(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
}
