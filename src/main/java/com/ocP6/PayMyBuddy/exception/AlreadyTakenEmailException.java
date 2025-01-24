package com.ocP6.PayMyBuddy.exception;



public class AlreadyTakenEmailException extends RuntimeException {

    public AlreadyTakenEmailException(){
        super("AlreadyTakenEmailException");
    }

    public AlreadyTakenEmailException(String message){
        super(message);
    }

}
