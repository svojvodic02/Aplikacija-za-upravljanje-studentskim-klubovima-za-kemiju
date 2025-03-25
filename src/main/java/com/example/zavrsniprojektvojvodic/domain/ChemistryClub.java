package com.example.zavrsniprojektvojvodic.domain;

import java.util.List;
import java.util.Set;

public non-sealed class ChemistryClub extends NamedEntity implements Countable{

    private List<Experiment> listOfExperiments;

    private List<Student> listOfStudents;

    public ChemistryClub(String name, Long id, List<Experiment> listOfExperiments, List<Student> listOfStudents) {
        super(name, id);
        this.listOfExperiments = listOfExperiments;
        this.listOfStudents = listOfStudents;
    }

    public List<Experiment> getListOfExperiments() {
        return listOfExperiments;
    }

    public void setListOfExperiments(List<Experiment> listOfExperiments) {
        this.listOfExperiments = listOfExperiments;
    }

    public List<Student> getListOfStudents() {
        return listOfStudents;
    }

    public void setListOfStudents(List<Student> listOfStudents) {
        this.listOfStudents = listOfStudents;
    }

    @Override
    public Integer howManyMembers(){
        return listOfStudents.size();
    }
    @Override
    public Integer howManyExperiments(){
        return listOfExperiments.size();
    }

}
