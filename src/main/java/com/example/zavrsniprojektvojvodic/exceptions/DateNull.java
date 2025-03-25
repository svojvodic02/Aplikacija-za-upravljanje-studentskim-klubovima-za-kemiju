package com.example.zavrsniprojektvojvodic.exceptions;

//oznacena
public class DateNull extends RuntimeException{
    public DateNull(){
        super("Starting date needs to be written!");
    }

    public DateNull(String message) {
        super(message);
    }

    public DateNull(String message, Throwable cause) {
        super(message, cause);
    }

    public DateNull(Throwable cause) {
        super(cause);
    }
}
