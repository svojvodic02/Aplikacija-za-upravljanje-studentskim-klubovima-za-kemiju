package com.example.zavrsniprojektvojvodic.domain;

public sealed interface Countable permits ChemistryClub{
    public Integer howManyExperiments();
    public Integer howManyMembers();
}
