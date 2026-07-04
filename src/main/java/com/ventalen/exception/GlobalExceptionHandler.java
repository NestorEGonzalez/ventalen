package com.ventalen.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler {
    
    @ExceptionHandler({ErrorCategoriaYaExistente.class, ErrorNombreProductoExistente.class, ErrorUsuarioNoValido.class})
    public ResponseEntity<ErrorResponse> duplicado(RuntimeException ex){
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
   }

    @ExceptionHandler({ErrorCategoriaInexistente.class, ErrorProductoConIdInexistente.class,
                       ErrorStockConIdInexistente.class, ErrorClienteInexistente.class,
                       ErrorProveedorInexistente.class, ErrorVentaInexistente.class,
                       ErrorIngresoInexistente.class, ErrorAjusteStockInexistente.class,
                       ErrorMotivoInexistente.class})
    public ResponseEntity<ErrorResponse> noEncontrado(RuntimeException ex){
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
   }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> campoVacioONulo(MethodArgumentNotValidException ex){
        String msj = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildResponse(msj, HttpStatus.BAD_REQUEST);
   }

   @ExceptionHandler({ErrorCampoVacioONulo.class, ErrorPrecioInvalido.class, ErrorCantidadInvalida.class, ErrorStockInsuficiente.class})
   public ResponseEntity<ErrorResponse> validacion(RuntimeException ex){
    return buildResponse(ex.getMessage(),HttpStatus.BAD_REQUEST);
   }

    private ResponseEntity<ErrorResponse> buildResponse(String mensaje, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(
                                status.value(),
                                mensaje,
                                System.currentTimeMillis()
                            );
        return new ResponseEntity<>(errorResponse,status);
   }

}
