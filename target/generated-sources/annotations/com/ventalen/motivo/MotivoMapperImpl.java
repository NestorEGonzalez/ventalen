package com.ventalen.motivo;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-25T11:24:53-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Debian)"
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
