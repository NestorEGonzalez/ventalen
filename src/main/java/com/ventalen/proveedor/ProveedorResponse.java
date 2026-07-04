package com.ventalen.proveedor;

public record ProveedorResponse(
    Long id, 
    String proveedor, 
    String corredor, 
    String telefono)
    {
}
