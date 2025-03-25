package com.example.zavrsniprojektvojvodic.domain;

import java.util.List;

public class Student extends NamedEntity{

    private String lastName;

    private String OIB;

   private String isStudentAlreadyInClub;

    public Student(String name, Long id, String lastName, String OIB) {
        super(name, id);
        this.lastName = lastName;
        this.OIB = OIB;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOIB() {
        return OIB;
    }

    public void setOIB(String OIB) {
        this.OIB = OIB;
    }
}
