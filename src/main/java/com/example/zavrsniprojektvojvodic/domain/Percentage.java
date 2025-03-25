package com.example.zavrsniprojektvojvodic.domain;

public class Percentage <T>{

    T data;

    public T getData(){
        return data;
    }

    public int getPercentageGeneric(T numerator){

        int percentage = (int) (0.5d + ((double)numerator/(double)100) * 100);

        return percentage;
    }
}
