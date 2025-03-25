package com.example.zavrsniprojektvojvodic.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

public class CurrentExperiment{


    private static CurrentExperiment instance;
    private String name;
    private Long id;
    private List<Student> Authors;

    private LocalDate dateStarted;

    private LocalDate dateFinished;

    private String Notes="";

    private Integer Progress;
    private CurrentExperiment(String currentExperimentName,Long currentExperimentId,List<Student> currentAuthors,LocalDate currentDateStarted,
                              LocalDate currentDateFinished,String currentNotes,Integer currentProgress) {
        this.name = currentExperimentName;
        this.id=currentExperimentId;
        this.Authors=currentAuthors;
        this.dateStarted=currentDateStarted;
        this.dateFinished=currentDateFinished;
        this.Notes=currentNotes;
        this.Progress=currentProgress;
    }

    public static CurrentExperiment getInstace(String currentExperimentName,Long currentExperimentID,List<Student> currentAuthors,LocalDate currentDateStarted,
                                               LocalDate currentDateFinished,String currentNotes,Integer currentProgress) {
        if(instance == null) {
            instance = new CurrentExperiment(currentExperimentName,currentExperimentID,currentAuthors,currentDateStarted,
                    currentDateFinished,currentNotes,currentProgress);
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public List<Student> getAuthors() {
        return Authors;
    }

    public LocalDate getDateStarted() {
        return dateStarted;
    }

    public LocalDate getDateFinished() {
        return dateFinished;
    }

    public String getNotes() {
        return Notes;
    }

    public Integer getProgress() {
        return Progress;
    }

    public void cleanUserSession() {
        name = "";// or null
        id=null;
        Authors=new ArrayList<>();
        dateStarted=null;
        dateFinished=null;
        Notes="";
        Progress=null;
    }
    
}
