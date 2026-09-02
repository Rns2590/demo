package com.example.demo.dto.producto;

import java.util.List;

/**
 * Contrato de paginación propio hacia el cliente de esta API. Internamente
 * reusa los parámetros limit/skip que ya expone DummyJSON, pero el shape de
 * la respuesta es el nuestro: podríamos cambiar de proveedor externo sin
 * romper este contrato.
 */
public record ProductoPageResponse(
        List<ProductoDTO> productos,
        int total,
        int limit,
        int skip
) {
}
