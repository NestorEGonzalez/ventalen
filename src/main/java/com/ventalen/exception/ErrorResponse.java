package com.ventalen.exception;

public record ErrorResponse (
    int status,
    String mensaje,
    long timestamp
){
    
}
