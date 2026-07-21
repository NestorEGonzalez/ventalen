package com.ventalen.stock;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.ventalen.exception.ErrorCantidadInvalida;
import com.ventalen.exception.ErrorStockConIdInexistente;
import com.ventalen.exception.ErrorStockYaExistente;
import com.ventalen.funciones.ValidarCantidad;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;

@Service
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final ProductoService productoService;

    public StockService(StockRepository stockRepository, ProductoService productoService){
        this.stockRepository = stockRepository;
        this.productoService = productoService;
    }

    @Transactional
    public Stock crearStock(Long idProd, Integer cantidad){
        ValidarCantidad.validarCantidad(cantidad);
        Producto producto = productoService.buscarProductoConId(idProd);
        if (stockRepository.existsByProducto(producto)){
            throw new ErrorStockYaExistente(producto.getId());
        }
        return stockRepository.save(new Stock(producto, cantidad));
    }

    /*private void validarCantidad(Integer cantidad){
        if (cantidad == null) {
            throw new ErrorCantidadInvalida(ErrorCantidadInvalida.ERROR_CANTIDAD_NULA);
        }
        if (cantidad < 0) {
            throw new ErrorCantidadInvalida(ErrorCantidadInvalida.ERROR_CANTIDAD_NEGATIVA);
        }
    }*/

    public List<Stock> obtenerTodosLosStocks(){
        return stockRepository.findAll();
    }

    public Stock buscarStockConId(Long id){
        return stockRepository.findById(id)
        .orElseThrow(()->{throw new ErrorStockConIdInexistente(id);});
    }

    public Stock buscarStockPorProducto(Long idProd){
        Producto producto = productoService.buscarProductoConId(idProd);
        return stockRepository.findByProducto(producto)
                                .orElseThrow(()->{
                                    throw new ErrorStockConIdInexistente(producto.getId());});
    }

    @Transactional
    public void borrarStockConId(Long id){
        verificarStock(id);
        stockRepository.deleteById(id);
    }

    private void verificarStock(Long id){
        if (!stockRepository.existsById(id)){
            throw new ErrorStockConIdInexistente(id);
        }
    }

    @Transactional
    public Stock cambiarCantidad(Long id, Integer cantidad){
        Stock stock = buscarStockConId(id);
        ValidarCantidad.validarCantidad(cantidad);
        stock.setCantidad(cantidad);
        return stockRepository.save(stock);
    }

    /*@Transactional
    public Stock modificarStock(Long id, Long productoId, Integer cantidad){
        Stock stock = buscarStockConId(id);
        if (!stock.getProducto().getId().equals(productoId)){
            Producto nuevoProducto = productoService.buscarProductoConId(productoId);
            if (stockRepository.existsByProducto(nuevoProducto)){
                throw new ErrorStockYaExistente(nuevoProducto.getId());
            }
            stock.setProducto(nuevoProducto);
        }
        validarCantidad(cantidad);
        stock.setCantidad(cantidad);
        return stockRepository.save(stock);
    }*/

}
