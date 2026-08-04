 package com.ventalen.venta;

import java.util.List;

//import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.cliente.Cliente;
import com.ventalen.cliente.ClienteRepository;
import com.ventalen.detalleVenta.DetalleVenta;
import com.ventalen.detalleVenta.DetalleVentaRepository;
import com.ventalen.detalleVenta.DetalleVentaRequest;
import com.ventalen.exception.ErrorClienteInactivo;
import com.ventalen.exception.ErrorClienteInexistente;
import com.ventalen.exception.ErrorStockInsuficiente;
import com.ventalen.exception.ErrorUsuarioNoValido;
import com.ventalen.exception.ErrorVentaInexistente;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@Service
@Transactional(readOnly = true)
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoService productoService;
    private final StockRepository stockRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        ProductoService productoService,
                        StockRepository stockRepository,
                        UsuarioRepository usuarioRepository,
                        ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoService = productoService;
        this.stockRepository = stockRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ErrorVentaInexistente(id));
    }

    @Transactional
    public Venta crear(Boolean pagado, Long clienteId, List<DetalleVentaRequest> detallesRequest) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Venta venta = new Venta(usuario, pagado);

        if (clienteId != null) {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new ErrorClienteInexistente(clienteId));
            if (!cliente.getActivo()) {
                throw new ErrorClienteInactivo(clienteId);
            }
            venta.setCliente(cliente);
        }

        for (DetalleVentaRequest detReq : detallesRequest) {
            Producto producto = productoService.buscarProductoConId(detReq.productoId());
            
            //Stock stock = stockRepository.findByProducto(producto).orElse(null);
            Stock stock = stockRepository.findByProducto(producto)
                    .orElseThrow(() -> new ErrorStockInsuficiente(producto.getId(), 0, detReq.cantidad()));

            int disponible = (stock != null) ? stock.getCantidad() : 0;
            if (disponible < detReq.cantidad()) {
                throw new ErrorStockInsuficiente(producto.getId(), disponible, detReq.cantidad());
            }

            stock.setCantidad(stock.getCantidad() - detReq.cantidad());
            stockRepository.save(stock);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(detReq.cantidad());
            detalle.setPrecioVenta(detReq.precioVenta());
            detalle.setStock(stock);
            venta.addDetalle(detalle);
        }

        return ventaRepository.save(venta);
    }

    private Usuario obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_INEXISTENTE));
    }

    public Venta pagar(Long id) {
        Venta ven = ventaRepository.findById(id)
                .orElseThrow(() -> new ErrorVentaInexistente(id));  
        ven.pagar();
        return ventaRepository.save(ven);
    }
}
