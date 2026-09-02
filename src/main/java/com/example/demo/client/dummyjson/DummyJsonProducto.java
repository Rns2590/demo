package com.example.demo.client.dummyjson;

/**
 * Representa un producto TAL COMO lo devuelve DummyJSON
 * (https://dummyjson.com/products). Los nombres de campo son los del JSON
 * externo a propósito: esta clase existe solo para deserializar la
 * respuesta y nunca sale del paquete "client". El resto de la app trabaja
 * con com.example.demo.dto.producto.ProductoDTO.
 */
public record DummyJsonProducto(
        Long id,
        String title,
        String description,
        String category,
        String brand,
        double price,
        double discountPercentage,
        int stock,
        double rating,
        String thumbnail
) {
}
