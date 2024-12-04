package com.ocP6.PayMyBuddy.exception;



public class ConflictException extends RuntimeException {

    public ConflictException(){
        super("Conflict");
    }

    public ConflictException(String message){
        super(message);
    }

}
