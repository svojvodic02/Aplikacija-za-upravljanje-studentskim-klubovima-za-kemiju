package com.example.zavrsniprojektvojvodic.generics;

import java.util.List;

public class Size <T>{

    List<T> howManyInList;

    public List<T> getHowManyInList() {
        return howManyInList;
    }

    public int getHowManyGeneric(List<T> data){
        int size=data.size();

        return size;
    }
}
