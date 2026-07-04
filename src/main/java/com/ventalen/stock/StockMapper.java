package com.ventalen.stock;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "productoId", source = "producto.id")
    StockResponse toResponse(Stock stock);

    List<StockResponse> toResponseList(List<Stock> stocks);

}
