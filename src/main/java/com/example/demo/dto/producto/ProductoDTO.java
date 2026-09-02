package com.example.demo.dto.producto;

/**
 * Contrato propio de esta API para un producto — no es el JSON de DummyJSON
 * tal cual. Elegimos y renombramos los campos que nos interesan: por
 * ejemplo, "title" pasa a llamarse "nombre" y "thumbnail" pasa a llamarse
 * "imagenUrl". El resto de la app nunca ve los nombres de campo originales.
 */
public record ProductoDTO(
        Long id,
        String nombre,
        String descripcion,
        String categoria,
        String marca,
        double precio,
        double descuentoPorcentaje,
        int stock,
        double calificacion,
        String imagenUrl
) {
}
