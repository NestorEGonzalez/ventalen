package com.ventalen.cliente;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.exception.ErrorClienteInexistente;
import com.ventalen.funciones.ValidarString;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ErrorClienteInexistente(id));
    }

    @Transactional
    public Cliente crear(String nyap, String telefono) {
        return clienteRepository.save(new Cliente(nyap, telefono));
    }

    @Transactional
    public Cliente modificar(Long id, String nyap, String telefono) {
        Cliente cliente = buscarPorId(id);
        //String nombre = ValidarString.validarString(nyap);
    /*
        if (!cliente.getNyap().equals(nombre)){
            cliente.setNyap(nyap);
        }
        if (!cliente.getTelefono().equals(telefono)){
        }   cliente.setTelefono(telefono);
    */  
        cambiarNombre(cliente, nyap);
        cambiarTelefono(cliente, telefono);
        cliente.activarCliente();
            
        return clienteRepository.save(cliente);
    }

    private void cambiarNombre(Cliente cliente, String nyap){
        String nombre = ValidarString.validarString(nyap);
        if (!cliente.getNyap().equals(nombre)){
            cliente.setNyap(nombre);
        }
    }

    private void cambiarTelefono(Cliente cliente, String telefono){
        if (!cliente.getTelefono().equals(telefono)){
            cliente.setTelefono(telefono);
        }
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.desactivarCliente();
        clienteRepository.save(cliente);
    }
}
