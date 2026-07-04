package com.ventalen.proveedor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.exception.ErrorProveedorInexistente;

@Service
@Transactional(readOnly = true)
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAllByActivoTrue();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ErrorProveedorInexistente(id));
    }

    @Transactional
    public Proveedor crear(String proveedor, String corredor, String telefono) {
        return proveedorRepository.save(new Proveedor(proveedor, corredor, telefono));
    }

    @Transactional
    public Proveedor modificar(Long id, String proveedor, String corredor, String telefono) {
        Proveedor prov = buscarPorId(id);
        prov.setProveedor(proveedor);
        prov.setCorredor(corredor);
        prov.setTelefono(telefono);
        prov.activarProveedor();;

        return proveedorRepository.save(prov);
    }

    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = buscarPorId(id);
        proveedor.desactivarProveedor();
        proveedorRepository.save(proveedor);
    }
}
