package com.ventalen.exception;

public class ErrorStockConIdInexistente extends RuntimeException{
    public ErrorStockConIdInexistente(Long idStock){
        super("El stock con id " + idStock + ", no existe.");
    }

}
