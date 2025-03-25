package com.example.zavrsniprojektvojvodic.exceptions;

//neoznacena
public class NotInTheStudentRecords extends RuntimeException{
    public NotInTheStudentRecords(){
        super("Student is not in the record therefore the account cannot be created!");
    }

    public NotInTheStudentRecords(String message) {
        super(message);
    }

    public NotInTheStudentRecords(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInTheStudentRecords(Throwable cause) {
        super(cause);
    }
}
