package com.example.demo.service;

import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.dto.producto.ProductoPageResponse;

/**
 * Contrato de la lógica de negocio del catálogo de productos. El controller
 * depende de esta interfaz, no de ProductoServiceImpl — así el mismo
 * controller serviría igual si mañana cambia el proveedor externo.
 */
public interface ProductoService {
    ProductoPageResponse listar(int limit, int skip);
    ProductoDTO obtenerPorId(Long id);
}
