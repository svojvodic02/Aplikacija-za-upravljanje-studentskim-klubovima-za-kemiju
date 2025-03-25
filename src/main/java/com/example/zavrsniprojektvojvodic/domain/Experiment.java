package com.example.zavrsniprojektvojvodic.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Experiment extends NamedEntity implements Progress{

    private List<Student> Authors;

    private LocalDate dateStarted;

    private LocalDate dateFinished;

    private String Notes="";

    private Integer Progress;

    private Experiment(String name, Long id, List<Student> authors, LocalDate dateStarted, LocalDate dateFinished, String notes,Integer progress) {
        super(name, id);
        Authors = authors;
        this.dateStarted = dateStarted;
        this.dateFinished = dateFinished;
        Notes = notes;
        this.Progress=progress;
    }

    public static class Builder{
        private String name;
        private Long id;
        private List<Student> authors;
        private LocalDate dateStarted;
        private LocalDate dateFinished;
        private String notes;
        private Integer progress;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setAuthors(List<Student> authors) {
            this.authors = authors;
            return this;
        }

        public Builder setDateStarted(LocalDate dateStarted) {
            this.dateStarted = dateStarted;
            return this;
        }

        public Builder setDateFinished(LocalDate dateFinished) {
            this.dateFinished = dateFinished;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setProgress(Integer progress) {
            this.progress = progress;
            return this;
        }

        public Experiment createExperiment() {
            return new Experiment(name, id, authors, dateStarted, dateFinished, notes, progress);
        }
    }

    public List<Student> getAuthors() {
        return Authors;
    }

    public void setAuthors(List<Student> authors) {
        Authors = authors;
    }

    public LocalDate getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(LocalDate dateStarted) {
        this.dateStarted = dateStarted;
    }

    public LocalDate getDateFinished() {
        return dateFinished;
    }

    public Optional<LocalDate> getDateFinishedOptional() {
        return Optional.ofNullable(dateFinished);
    }

    public void setDateFinished(LocalDate dateFinished) {
        this.dateFinished = dateFinished;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public Integer getProgress() {
        return Progress;
    }

    public void setProgress(Integer progress) {
        Progress = progress;
    }

    public int getProgressPercentage(Long number) {
         Percentage<Long> genericClassPercentage=new Percentage<>();
         int neededPercentage=genericClassPercentage.getPercentageGeneric(number);
         return neededPercentage;
    }
}
