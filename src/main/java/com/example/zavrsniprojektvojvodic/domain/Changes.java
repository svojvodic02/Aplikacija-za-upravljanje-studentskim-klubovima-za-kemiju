package com.example.zavrsniprojektvojvodic.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Changes<T1,T2> implements Serializable {

    private T1 whoDidTheChange;

    private T1 whatWasTheChange;

   private T2 whatTimeTheChangeOccured;
   private T2 whatDateTheChangeOccured;

    public Changes(T1 whoDidTheChange, T1 whatWasTheChange, T2 whatTimeTheChangeOccured, T2 whatDateTheChangeOccured) {
        this.whoDidTheChange = whoDidTheChange;
        this.whatWasTheChange = whatWasTheChange;
        this.whatTimeTheChangeOccured = whatTimeTheChangeOccured;
        this.whatDateTheChangeOccured = whatDateTheChangeOccured;
    }

    public T1 getWhoDidTheChange() {
        return whoDidTheChange;
    }

    public void setWhoDidTheChange(T1 whoDidTheChange) {
        this.whoDidTheChange = whoDidTheChange;
    }

    public T1 getWhatWasTheChange() {
        return whatWasTheChange;
    }

    public void setWhatWasTheChange(T1 whatWasTheChange) {
        this.whatWasTheChange = whatWasTheChange;
    }

    public T2 getWhatTimeTheChangeOccured() {
        return whatTimeTheChangeOccured;
    }

    public void setWhatTimeTheChangeOccured(T2 whatTimeTheChangeOccured) {
        this.whatTimeTheChangeOccured = whatTimeTheChangeOccured;
    }

    public T2 getWhatDateTheChangeOccured() {
        return whatDateTheChangeOccured;
    }

    public void setWhatDateTheChangeOccured(T2 whatDateTheChangeOccured) {
        this.whatDateTheChangeOccured = whatDateTheChangeOccured;
    }
}
