package com.ocP6.PayMyBuddy.exception;



public class ConflictExceedsException extends RuntimeException {

    public ConflictExceedsException(){
        super("ConflictExceeds");
    }

    public ConflictExceedsException(String message){
        super(message);
    }

}
