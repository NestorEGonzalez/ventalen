package com.ventalen.exception;

public class ErrorStockYaExistente extends RuntimeException{
    public ErrorStockYaExistente(Long idProd){
        super("Ya existe stock para el producto con id " + idProd + ".");
    }

}
