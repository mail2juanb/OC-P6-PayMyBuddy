package com.ocP6.PayMyBuddy.exception;



public class ConflictConnectionException extends RuntimeException {

    public ConflictConnectionException(){
        super("ConflictConnection");
    }

    public ConflictConnectionException(String message){
        super(message);
    }

}
