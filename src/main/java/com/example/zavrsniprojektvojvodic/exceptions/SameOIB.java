package com.example.zavrsniprojektvojvodic.exceptions;

//neoznacena
public class SameOIB extends Exception{

    public SameOIB(){
            super("That OIB is already in use!");
        }
        public SameOIB(String message) {
            super(message);
        }

        public SameOIB(String message, Throwable cause) {
            super(message, cause);
        }

        public SameOIB(Throwable cause) {
            super(cause);
        }

}
