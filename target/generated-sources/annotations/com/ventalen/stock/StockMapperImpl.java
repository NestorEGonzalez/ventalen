package com.ventalen.stock;

import com.ventalen.producto.Producto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T15:47:18-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class StockMapperImpl implements StockMapper {

    @Override
    public StockResponse toResponse(Stock stock) {
        if ( stock == null ) {
            return null;
        }

        Long productoId = null;
        Long id = null;
        Integer cantidad = null;

        productoId = stockProductoId( stock );
        id = stock.getId();
        cantidad = stock.getCantidad();

        StockResponse stockResponse = new StockResponse( id, productoId, cantidad );

        return stockResponse;
    }

    @Override
    public List<StockResponse> toResponseList(List<Stock> stocks) {
        if ( stocks == null ) {
            return null;
        }

        List<StockResponse> list = new ArrayList<StockResponse>( stocks.size() );
        for ( Stock stock : stocks ) {
            list.add( toResponse( stock ) );
        }

        return list;
    }

    private Long stockProductoId(Stock stock) {
        if ( stock == null ) {
            return null;
        }
        Producto producto = stock.getProducto();
        if ( producto == null ) {
            return null;
        }
        Long id = producto.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
