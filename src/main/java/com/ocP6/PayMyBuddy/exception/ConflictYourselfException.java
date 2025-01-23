package com.ocP6.PayMyBuddy.exception;



public class ConflictYourselfException extends RuntimeException {

    public ConflictYourselfException(){
        super("ConflictYourself");
    }

    public ConflictYourselfException(String message){
        super(message);
    }

}
