package com.micronimbus.doctorshaheb;



import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiseaseDetector {

    private final Map<String, Disease> diseaseMap = new HashMap<>();

    public DiseaseDetector(Context context) {

        diseaseMap.put("Malaria", new Disease(
                "Malaria",
                Arrays.asList("fever", "chills", "sweating", "headache", "muscle pain", "joint pain", "nausea", "vomiting", "fatigue", "weakness", "rapid heartbeat", "anemia"),
                Arrays.asList(
                        "Rest and stay hydrated",
                        "Use mosquito nets and repellents",
                        "Eat nutritious food",
                        "Avoid self-medication, consult a doctor"
                ),
                Arrays.asList(
                        "Artemether + Lumefantrine (Coartem, Riamet)",
                        "Chloroquine (if no resistance)",
                        "Quinine sulfate",
                        "Paracetamol (Napa, Ace, etc.)",
                        "ORS (Oral Saline)"
                )
        ));


        diseaseMap.put("Kala Azar", new Disease(
                "Kala Azar",
                Arrays.asList("fever", "weight loss", "abdominal swelling", "dark skin", "fatigue", "loss of appetite", "anemia", "night sweats"),
                Arrays.asList(
                        "Consult a doctor immediately",
                        "Use bed nets to avoid sandfly bites",
                        "Maintain hygiene",
                        "Take full course of treatment"
                ),
                Arrays.asList(
                        "Sodium Stibogluconate (Pentostam)",
                        "Amphotericin B (AmBisome)",
                        "Miltefosine (Impavido)",
                        "Paromomycin",
                        "Paracetamol for fever"
                )
        ));


        diseaseMap.put("Dengue", new Disease(
                "Dengue",
                Arrays.asList("fever", "headache", "pain behind the eyes", "joint pain", "muscle pain", "rash", "bleeding", "fatigue", "vomiting"),
                Arrays.asList(
                        "Stay hydrated with water or ORS",
                        "Avoid aspirin or ibuprofen",
                        "Take rest",
                        "Visit hospital if symptoms worsen"
                ),
                Arrays.asList(
                        "Paracetamol (Napa, Ace, etc.)",
                        "ORS",
                        "Ondansetron (for vomiting)",
                        "IV fluids (in hospital)",
                        "Platelet transfusion (severe cases)"
                )
        ));


    }

    public String detectDisease(List<String> matchedSymptoms) {
        String likelyDisease = null;
        int maxMatches = 0;

        for (Map.Entry<String, Disease> entry : diseaseMap.entrySet()) {
            Disease disease = entry.getValue();
            int matches = 0;
            for (String symptom : matchedSymptoms) {
                if (disease.symptoms.contains(symptom)) {
                    matches++;
                }
            }
            if (matches > maxMatches && matches >= 3) {
                maxMatches = matches;
                likelyDisease = disease.name;
            }
        }

        return likelyDisease;
    }

    public Disease getDiseaseInfo(String diseaseName) {
        return diseaseMap.get(diseaseName);
    }
}
