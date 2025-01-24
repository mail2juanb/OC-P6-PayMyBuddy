package com.ocP6.PayMyBuddy.exception;



public class AlreadyTakenUsernameException extends RuntimeException {

    public AlreadyTakenUsernameException(){
        super("AlreadyTakenUsernameException");
    }

    public AlreadyTakenUsernameException(String message){
        super(message);
    }

}
