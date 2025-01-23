package com.ocP6.PayMyBuddy.exception;



public class NotFoundRelationException extends RuntimeException {

    public NotFoundRelationException(){
        super("RelationNotFound");
    }

    public NotFoundRelationException(String message){
        super(message);
    }

}
