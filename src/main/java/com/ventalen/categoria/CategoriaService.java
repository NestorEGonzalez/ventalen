package com.ventalen.categoria;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ventalen.exception.ErrorCategoriaInexistente;
import com.ventalen.exception.ErrorCategoriaYaExistente;
import com.ventalen.exception.ErrorNombreDeCategoriaInexistente;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria crearCategoria(String nombreCategoria) {
        verificarCategoriaExistente(nombreCategoria);
        return categoriaRepository.save(new Categoria(nombreCategoria));
    }

    private void verificarCategoriaExistente(String nombre) {
        if (categoriaRepository.existsByCategoria(nombre.trim().toLowerCase())) {
            throw new ErrorCategoriaYaExistente(nombre.trim().toLowerCase());
        }
        
    }

	public Optional<Categoria> buscarCategoriaPorNombre(String nombreCategoria) {
		return categoriaRepository.findOneByCategoriaIgnoreCase(nombreCategoria);
	}

    @Transactional
    public void eliminarCategoria(Long id) {
        verificarIdExistente(id);
        categoriaRepository.deleteById(id);;
    }

    private void verificarIdExistente(Long id) {
        if (!categoriaRepository.existsById(id)){
            throw new ErrorCategoriaInexistente(id);

        }
    }

    @Transactional
    public void cambiarNombreDeCategoria(String nombreActual, String nombreNuevo) {
        verificarCategoriaExistente(nombreNuevo);
        Categoria cat = categoriaRepository
                            .findOneByCategoriaIgnoreCase(nombreActual)
                            .orElseThrow(()->{
                                throw new  ErrorNombreDeCategoriaInexistente(nombreActual);
                            });
        cat.setCategoria(nombreNuevo);
        categoriaRepository.save(cat);
    }

    @Transactional
    public Categoria cambiarNombreDeCategoriaPorId(Long id, String nombreNuevo){
        Categoria cat = categoriaRepository
                        .findById(id)
                        .orElseThrow(()->{
                            throw new ErrorCategoriaInexistente(id);
                        });
        verificarCategoriaExistente(nombreNuevo);
        cat.setCategoria(nombreNuevo);
        return categoriaRepository.save(cat);
    }

    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> buscarCategoriasQueContengan(String cat) {
        return categoriaRepository.findAllByCategoriaContainingIgnoreCase(cat);
    }

    public Categoria buscarCategoriaPorId(Long id) {
        Categoria cat = categoriaRepository.findById(id).orElseThrow(()->{
            throw new ErrorCategoriaInexistente(id);
        });
        return cat;
    }

}
