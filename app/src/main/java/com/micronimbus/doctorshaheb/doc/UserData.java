package com.micronimbus.doctorshaheb.doc;

public class UserData {

    String name, email, password, dob, country, phone, bloodGroup;  // Added bloodGroup

    // Default constructor required for Firebase
    public UserData() { }

    // Constructor with blood group
    public UserData(String name, String email, String password, String dob, String country, String phone, String bloodGroup) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.country = country;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
    }

    // Getters and Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBloodGroup() { return bloodGroup; }  // New getter
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }  // New setter
}
