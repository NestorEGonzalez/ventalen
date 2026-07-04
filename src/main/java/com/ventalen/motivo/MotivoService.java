package com.ventalen.motivo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.exception.ErrorMotivoInexistente;

@Service
@Transactional(readOnly = true)
public class MotivoService {

    private final MotivoRepository motivoRepository;

    public MotivoService(MotivoRepository motivoRepository) {
        this.motivoRepository = motivoRepository;
    }

    public List<Motivo> obtenerTodos() {
        return motivoRepository.findAll();
    }

    public Motivo buscarPorId(Long id) {
        return motivoRepository.findById(id)
                .orElseThrow(() -> new ErrorMotivoInexistente(id));
    }

    @Transactional
    public Motivo crear(String motivo, Boolean afectaPositivo) {
        return motivoRepository.save(new Motivo(motivo, afectaPositivo));
    }

    @Transactional
    public Motivo modificar(Long id, String motivo, Boolean afectaPositivo) {
        Motivo entidad = buscarPorId(id);
        entidad.setMotivo(motivo);
        if (afectaPositivo != null) {
            entidad.setAfectaPositivo(afectaPositivo);
        }
        return motivoRepository.save(entidad);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!motivoRepository.existsById(id)) {
            throw new ErrorMotivoInexistente(id);
        }
        motivoRepository.deleteById(id);
    }
}
