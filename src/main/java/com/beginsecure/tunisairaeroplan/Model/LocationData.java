package com.beginsecure.tunisairaeroplan.Model;

import java.util.*;

public class LocationData {
    private static final Map<String, List<String>> countryAirports = new HashMap<>();

    static {
        // Tunisie
        countryAirports.put("Tunisie", Arrays.asList(
                "Aéroport International de Tunis-Carthage",
                "Aéroport International de Monastir",
                "Aéroport International de Djerba-Zarzis",
                "Aéroport de Tozeur-Nefta",
                "Aéroport de Sfax-Thyna"
        ));

        countryAirports.put("France", Arrays.asList(
                "Aéroport Charles de Gaulle (Paris)",
                "Aéroport d'Orly (Paris)",
                "Aéroport de Nice Côte d'Azur",
                "Aéroport de Lyon-Saint-Exupéry",
                "Aéroport de Marseille Provence"
        ));
        countryAirports.put("Allemagne", Arrays.asList(
                "Aéroport de Francfort-sur-le-Main",
                "Aéroport de Munich",
                "Aéroport de Berlin-Brandenburg",
                "Aéroport de Düsseldorf",
                "Aéroport de Hambourg"
        ));

        countryAirports.put("Italie", Arrays.asList(
                "Aéroport Léonard-de-Vinci de Rome Fiumicino",
                "Aéroport de Milan Malpensa",
                "Aéroport de Venise-Marco-Polo",
                "Aéroport de Naples-Capodichino",
                "Aéroport de Bologne-Guglielmo-Marconi"
        ));

        countryAirports.put("Espagne", Arrays.asList(
                "Aéroport Adolfo Suárez Madrid-Barajas",
                "Aéroport de Barcelone-El Prat",
                "Aéroport de Palma de Majorque",
                "Aéroport de Malaga-Costa del Sol",
                "Aéroport de Valence"
        ));

        countryAirports.put("Royaume-Uni", Arrays.asList(
                "Aéroport de Londres-Heathrow",
                "Aéroport de Londres-Gatwick",
                "Aéroport de Manchester",
                "Aéroport de Birmingham",
                "Aéroport d'Édimbourg"
        ));

        countryAirports.put("États-Unis", Arrays.asList(
                "Aéroport international de Hartsfield-Jackson d’Atlanta",
                "Aéroport international de Los Angeles (LAX)",
                "Aéroport international O'Hare de Chicago",
                "Aéroport international de Dallas-Fort Worth",
                "Aéroport international John-F.-Kennedy (New York)"
        ));

    }

    public static List<String> getCountries() {
        return new ArrayList<>(countryAirports.keySet());
    }

    public static List<String> getAirportsForCountry(String country) {
        return countryAirports.getOrDefault(country, Collections.emptyList());
    }

    public static final List<String> DEPARTEMENTS = Arrays.asList(
            "Ressources Humaines",
            "Maintenance",
            "Opérations Aériennes",
            "Sécurité",
            "Commercial",
            "Finances"
    );

    public static final List<String> POSTES = Arrays.asList(
            "Pilote",
            "Hôtesse de l'air/Steward",
            "Ingénieur de maintenance",
            "Agent de piste",
            "Contrôleur aérien",
            "Agent d'accueil",
            "Responsable de département"
    );

    public static final List<String> BASES_AFFECTATION = Arrays.asList(
            "Tunis-Carthage",
            "Djerba",
            "Monastir",
            "Sfax",
            "Tozeur"
    );
}
