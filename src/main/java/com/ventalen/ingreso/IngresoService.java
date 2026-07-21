package com.ventalen.ingreso;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.detalleIngreso.DetalleIngreso;
import com.ventalen.detalleIngreso.DetalleIngresoRepository;
import com.ventalen.detalleIngreso.DetalleIngresoRequest;
import com.ventalen.exception.ErrorIngresoInexistente;
import com.ventalen.exception.ErrorProveedorInexistente;
import com.ventalen.exception.ErrorUsuarioNoValido;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.proveedor.Proveedor;
import com.ventalen.proveedor.ProveedorRepository;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@Service
@Transactional(readOnly = true)
public class IngresoService {

    private final IngresoRepository ingresoRepository;
    private final DetalleIngresoRepository detalleIngresoRepository;
    private final ProductoService productoService;
    private final ProveedorRepository proveedorRepository;
    private final StockRepository stockRepository;
    private final UsuarioRepository usuarioRepository;

    public IngresoService(IngresoRepository ingresoRepository,
                          DetalleIngresoRepository detalleIngresoRepository,
                          ProductoService productoService,
                          ProveedorRepository proveedorRepository,
                          StockRepository stockRepository,
                          UsuarioRepository usuarioRepository) {
        this.ingresoRepository = ingresoRepository;
        this.detalleIngresoRepository = detalleIngresoRepository;
        this.productoService = productoService;
        this.proveedorRepository = proveedorRepository;
        this.stockRepository = stockRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Ingreso> obtenerTodos() {
        return ingresoRepository.findAll();
    }

    public Ingreso buscarPorId(Long id) {
        return ingresoRepository.findById(id)
                .orElseThrow(() -> new ErrorIngresoInexistente(id));
    }

    @Transactional
    public Ingreso crear(Long proveedorId, List<DetalleIngresoRequest> detallesRequest) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ErrorProveedorInexistente(proveedorId));

        Ingreso ingreso = new Ingreso(usuario, proveedor);

        for (DetalleIngresoRequest detReq : detallesRequest) {
            Producto producto = productoService.buscarProductoConId(detReq.productoId());
            Stock stock = stockRepository.findByProducto(producto)
                    .orElseGet(() -> stockRepository.save(new Stock(producto, 0)));

            stock.setCantidad(stock.getCantidad() + detReq.cantidad());
            stockRepository.save(stock);

            DetalleIngreso detalle = new DetalleIngreso();
            detalle.setProducto(producto);
            detalle.setCantidad(detReq.cantidad());
            detalle.setPrecioMayorista(detReq.precioMayorista());
            detalle.setStock(stock);
            ingreso.addDetalle(detalle);
        }

        return ingresoRepository.save(ingreso);
    }

    private Usuario obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_INEXISTENTE));
    }
}
