package com.example.zavrsniprojektvojvodic.exceptions;

//oznacena
public class ProgressNotANumber extends Exception{

    public ProgressNotANumber() {
        super("Progress must be a number!");
    }

    public ProgressNotANumber(String message) {
        super(message);
    }

    public ProgressNotANumber(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgressNotANumber(Throwable cause) {
        super(cause);
    }


}
