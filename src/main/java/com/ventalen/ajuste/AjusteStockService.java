package com.ventalen.ajuste;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.auth.Usuario;
import com.ventalen.auth.UsuarioRepository;
import com.ventalen.exception.ErrorAjusteStockInexistente;
import com.ventalen.exception.ErrorUsuarioNoValido;
import com.ventalen.motivo.Motivo;
import com.ventalen.motivo.MotivoRepository;
import com.ventalen.producto.Producto;
import com.ventalen.producto.ProductoService;
import com.ventalen.stock.Stock;
import com.ventalen.stock.StockRepository;

@Service
@Transactional(readOnly = true)
public class AjusteStockService {

    private final AjusteStockRepository ajusteStockRepository;
    private final ProductoService productoService;
    private final MotivoRepository motivoRepository;
    private final StockRepository stockRepository;
    private final UsuarioRepository usuarioRepository;

    public AjusteStockService(AjusteStockRepository ajusteStockRepository,
                              ProductoService productoService,
                              MotivoRepository motivoRepository,
                              StockRepository stockRepository,
                              UsuarioRepository usuarioRepository) {
        this.ajusteStockRepository = ajusteStockRepository;
        this.productoService = productoService;
        this.motivoRepository = motivoRepository;
        this.stockRepository = stockRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<AjusteStock> obtenerTodos() {
        return ajusteStockRepository.findAll();
    }

    public AjusteStock buscarPorId(Long id) {
        return ajusteStockRepository.findById(id)
                .orElseThrow(() -> new ErrorAjusteStockInexistente(id));
    }

    @Transactional
    public AjusteStock crear(Long productoId, Integer cantidad, Long motivoId, String detalles) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Producto producto = productoService.buscarProductoConId(productoId);
        Motivo motivo = motivoRepository.findById(motivoId)
                .orElseThrow(() -> new com.ventalen.exception.ErrorMotivoInexistente(motivoId));
        Stock stock = stockRepository.findByProducto(producto)
                .orElseGet(() -> stockRepository.save(new Stock(producto, 0)));

        int ajuste = motivo.getAfectaPositivo() ? cantidad : -cantidad;
        stock.setCantidad(stock.getCantidad() + ajuste);
        stockRepository.save(stock);

        AjusteStock ajusteStock = new AjusteStock();
        ajusteStock.setProducto(producto);
        ajusteStock.setCantidad(cantidad);
        ajusteStock.setMotivo(motivo);
        ajusteStock.setUsuario(usuario);
        ajusteStock.setDetalles(detalles);
        ajusteStock.setStock(stock);

        return ajusteStockRepository.save(ajusteStock);
    }

    private Usuario obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ErrorUsuarioNoValido(ErrorUsuarioNoValido.ERROR_USUARIO_INEXISTENTE));
    }
}
