package com.ventalen.motivo;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MotivoMapper {

    MotivoResponse toResponse(Motivo motivo);

    List<MotivoResponse> toResponseList(List<Motivo> motivos);
}
