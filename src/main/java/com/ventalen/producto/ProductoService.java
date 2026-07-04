package com.ventalen.producto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.categoria.Categoria;
import com.ventalen.categoria.CategoriaRepository;
import com.ventalen.exception.ErrorCampoVacioONulo;
import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorNombreProductoExistente;
import com.ventalen.exception.ErrorProductoConIdInexistente;
import com.ventalen.funciones.ValidarPrecio;
import com.ventalen.funciones.ValidarString;


@Service
@Transactional(readOnly = true)

public class ProductoService {

    private ProductoRepository productoRepository;

    private CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository){
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Producto crearProducto(String nombreProd, BigDecimal precio, Long idCat) {
        String nombre = ValidarString.validarString(nombreProd);
        BigDecimal precioValidado = ValidarPrecio.validarPrecio(precio);
        //verificarNombre(nombreProd);
        verificarNombreExistente(nombre);
        Categoria cat = obtenerCategoriaPorId(idCat);
        
        return productoRepository.save(new Producto(nombre,precioValidado,cat));
        
    }

    private Categoria obtenerCategoriaPorId(Long idCat) {
        Categoria cat = categoriaRepository
                        .findById(idCat)
                        .orElseThrow(()->{
                            throw new ErrorCategoriaInexistente(idCat);
                        });
        return cat;
    }

    /*private void verificarNombre(String nombreProd) {
        if (nombreProd == null) {
            throw new ErrorCampoVacioONulo(ErrorCampoVacioONulo.ERROR_CAMPO_VACIO_NULO);
        }
        
    }*/

    private void verificarNombreExistente(String nombreProd){
        if (productoRepository.existsByNombre(nombreProd.trim().toLowerCase())) {
            throw new ErrorNombreProductoExistente(nombreProd.trim().toLowerCase());
        }
    }

    @Transactional
    public void borrarProductoConId(Long idProd) {
        //verificarProducto(idProd);
        Producto producto = obtenerProductoConId(idProd);
        producto.desactivarProducto();
        productoRepository.save(producto);

        //productoRepository.deleteByI d(idProd);
    }

    /* private void verificarProducto(Long idProd) {
        if (!productoRepository.existsById(idProd)) {
            throw new ErrorProductoConIdInexistente(idProd);
        }
    } */

    @Transactional
    public void cambiarNombreDeProducto(Long idProd, String nombreNuevo) {
        Producto producto = obtenerProductoConId(idProd);
        //verificarNombre(nombreNuevo);
        String nuevoNombre =ValidarString.validarString(nombreNuevo);
        verificarNombreExistente(nuevoNombre);
        producto.setNombre(nuevoNombre);
        productoRepository.save(producto);
    }

    private Producto obtenerProductoConId(Long idProd) {
        Producto producto = productoRepository
                            .findById(idProd)
                            .orElseThrow(()->{
                                throw new ErrorProductoConIdInexistente(idProd);
                            });
        return producto;                    
    }

    @Transactional
    public void cambiarCategoriaDeProducto(Long idProd, Long idCat) {
        Producto producto = obtenerProductoConId(idProd);
        Categoria cat = obtenerCategoriaPorId(idCat);
        producto.setCategoria(cat);
        productoRepository.save(producto);
    }

    @Transactional
    public void cambiarPrecioDeProducto(Long idProd, BigDecimal precioNuevo) {
        Producto producto = obtenerProductoConId(idProd);
        producto.setPrecio(precioNuevo);
    }

    public List<Producto>obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public Producto buscarProductoConId(Long id) {
        return productoRepository.findById(id).orElseThrow(()->{throw new ErrorProductoConIdInexistente(id);});
    }

    public List<Producto> obtenerProductosDeCategoria(Long id) {
        Categoria cat = obtenerCategoriaPorId(id);
        return productoRepository.findAllByCategoria(cat);
    }

    @Transactional
    public void actualizarCategoriaDeProductos(Long idCatVieja, Long idCatNueva) {
        Categoria catVieja = obtenerCategoriaPorId(idCatVieja);
        Categoria catNueva = obtenerCategoriaPorId(idCatNueva);
        productoRepository.actualizarCategoria(catNueva, catVieja);
        
    }

    public List<Producto> obtenerProductosConNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);

    }

    @Transactional
    public Producto modificarDatosDeProducto(Long id, String nombre, BigDecimal precio, Long idCat) {
        Producto prod = obtenerProductoConId(id);
        if (!prod.getNombre().equals(nombre.trim().toLowerCase())) {
            cambiarNombreDeProducto(id, nombre);
        }
        if (!prod.getCategoria().getId().equals(idCat)) {
            cambiarCategoriaDeProducto(id, idCat);
        }
        cambiarPrecioDeProducto(id, precio);
        return productoRepository.save(prod);

    }
    
}
