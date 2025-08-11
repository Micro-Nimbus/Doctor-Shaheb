package com.micronimbus.doctorshaheb;


import java.util.List;

public class Disease {
    public String name;
    public List<String> symptoms;
    public List<String> recommendations;
    public List<String> medicines;

    public Disease(String name, List<String> symptoms, List<String> recommendations, List<String> medicines) {
        this.name = name;
        this.symptoms = symptoms;
        this.recommendations = recommendations;
        this.medicines = medicines;
    }
}
