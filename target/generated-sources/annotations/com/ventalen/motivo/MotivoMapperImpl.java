package com.ventalen.motivo;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T15:47:17-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class MotivoMapperImpl implements MotivoMapper {

    @Override
    public MotivoResponse toResponse(Motivo motivo) {
        if ( motivo == null ) {
            return null;
        }

        Long id = null;
        String motivo1 = null;
        Boolean afectaPositivo = null;

        id = motivo.getId();
        motivo1 = motivo.getMotivo();
        afectaPositivo = motivo.getAfectaPositivo();

        MotivoResponse motivoResponse = new MotivoResponse( id, motivo1, afectaPositivo );

        return motivoResponse;
    }

    @Override
    public List<MotivoResponse> toResponseList(List<Motivo> motivos) {
        if ( motivos == null ) {
            return null;
        }

        List<MotivoResponse> list = new ArrayList<MotivoResponse>( motivos.size() );
        for ( Motivo motivo : motivos ) {
            list.add( toResponse( motivo ) );
        }

        return list;
    }
}
