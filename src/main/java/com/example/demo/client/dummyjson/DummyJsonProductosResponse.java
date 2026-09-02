package com.example.demo.client.dummyjson;

import java.util.List;

/**
 * Forma exacta de la respuesta de GET https://dummyjson.com/products
 * (incluye "products", "total", "skip" y "limit").
 */
public record DummyJsonProductosResponse(
        List<DummyJsonProducto> products,
        int total,
        int skip,
        int limit
) {
}
