package com.example.zavrsniprojektvojvodic.domain;

public class ChemistryClubFilter {

    private String chemistryClubName;

    private Long chemistryClubId;

    public ChemistryClubFilter(String chemistryClubName, Long chemistryClubId) {
        this.chemistryClubName = chemistryClubName;
        this.chemistryClubId = chemistryClubId;
    }

    public String getChemistryClubName() {
        return chemistryClubName;
    }

    public void setChemistryClubName(String chemistryClubName) {
        this.chemistryClubName = chemistryClubName;
    }

    public Long getChemistryClubId() {
        return chemistryClubId;
    }

    public void setChemistryClubId(Long chemistryClubId) {
        this.chemistryClubId = chemistryClubId;
    }
}
