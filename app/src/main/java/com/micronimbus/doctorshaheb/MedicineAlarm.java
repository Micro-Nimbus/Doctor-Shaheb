package com.micronimbus.doctorshaheb;

public class MedicineAlarm {
    public int id;
    public String medicineName;
    public int hour;
    public int minute;

    public MedicineAlarm(int id, String medicineName, int hour, int minute) {
        this.id = id;
        this.medicineName = medicineName;
        this.hour = hour;
        this.minute = minute;
    }
}
