package com.ocP6.PayMyBuddy.exception;



public class NotFoundCustomerException extends RuntimeException {

    public NotFoundCustomerException(){
        super("CustomerNotFound");
    }

    public NotFoundCustomerException(String message){
        super(message);
    }

}
