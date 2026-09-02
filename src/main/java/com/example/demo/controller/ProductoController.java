package com.example.demo.controller;

import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.dto.producto.ProductoPageResponse;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catálogo de productos: solo lectura. Consume DummyJSON a través de
 * ProductoService y nunca expone el JSON externo tal cual (ver ProductoDTO).
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "productos", description = "Catálogo de productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar productos",
            description = "Devuelve una página del catálogo externo mapeada a nuestro propio contrato. "
                    + "limit/skip se reenvían a DummyJSON tal como llegan."
    )
    public ProductoPageResponse listar(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int skip) {
        return service.listar(limit, skip);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un producto por id",
            description = "Busca un producto puntual en el catálogo externo. Responde 404 si no existe."
    )
    public ProductoDTO obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }
}
