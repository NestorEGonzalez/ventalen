package com.ventalen.stock;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ventalen")
public class StockController {

    private final StockService stockService;
    private final StockMapper stockMapper;
    private static final String URL_BASE = "/stocks";

    public StockController(StockService stockService, StockMapper stockMapper){
        this.stockService = stockService;
        this.stockMapper = stockMapper;
    }

    @PostMapping(URL_BASE)
    public ResponseEntity<StockResponse> crearStock(@Valid @RequestBody StockRequest stockRequest){
        Stock stock = stockService.crearStock(stockRequest.productoId(), stockRequest.cantidad());
        StockResponse response = stockMapper.toResponse(stock);
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("{id}")
                        .buildAndExpand(stock.getId())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping(URL_BASE)
    public ResponseEntity<List<StockResponse>> obtenerTodosLosStocks(){
        List<StockResponse> lista = stockMapper.toResponseList(stockService.obtenerTodosLosStocks());
        return ResponseEntity.ok(lista);
    }

    @GetMapping(URL_BASE+"/{id}")
    public ResponseEntity<StockResponse> obtenerStockPorId(@PathVariable Long id){
        Stock stock = stockService.buscarStockConId(id);
        return ResponseEntity.ok(stockMapper.toResponse(stock));
    }

    @DeleteMapping(URL_BASE+"/{id}")
    public ResponseEntity<Void> borrarStock(@PathVariable Long id){
        stockService.borrarStockConId(id);
        return ResponseEntity.noContent().build();
    }

    /*@PatchMapping(URL_BASE+"/{id}")
    public ResponseEntity<StockResponse> modificarStock(@PathVariable Long id, @Valid @RequestBody StockRequest stockRequest){
        Stock stock = stockService.modificarStock(id, stockRequest.productoId(), stockRequest.cantidad());
        return ResponseEntity.accepted().body(stockMapper.toResponse(stock));
    }*/

}
