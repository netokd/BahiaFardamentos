package com.neto.bahiafardamentos.exception;

public class BandeiraNotFoundException
        extends RuntimeException{
    public BandeiraNotFoundException(String message){
        super(message);
    }
}
