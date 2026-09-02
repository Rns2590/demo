package com.example.demo.service;

import com.example.demo.client.dummyjson.DummyJsonClient;
import com.example.demo.client.dummyjson.DummyJsonProducto;
import com.example.demo.client.dummyjson.DummyJsonProductosResponse;
import com.example.demo.dto.producto.ProductoDTO;
import com.example.demo.dto.producto.ProductoPageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Traduce entre el contrato externo de DummyJSON y el contrato propio de
 * esta API (ProductoDTO). Ni el controller ni nadie fuera de este paquete +
 * "client" conoce los nombres de campo originales de DummyJSON.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private final DummyJsonClient dummyJsonClient;

    public ProductoServiceImpl(DummyJsonClient dummyJsonClient) {
        this.dummyJsonClient = dummyJsonClient;
    }

    @Override
    public ProductoPageResponse listar(int limit, int skip) {
        DummyJsonProductosResponse respuesta = dummyJsonClient.listar(limit, skip);
        List<ProductoDTO> productos = respuesta.products().stream()
                .map(this::aProductoDTO)
                .toList();
        return new ProductoPageResponse(productos, respuesta.total(), respuesta.limit(), respuesta.skip());
    }

    @Override
    public ProductoDTO obtenerPorId(Long id) {
        DummyJsonProducto producto = dummyJsonClient.obtenerPorId(id);
        return aProductoDTO(producto);
    }

    private ProductoDTO aProductoDTO(DummyJsonProducto externo) {
        return new ProductoDTO(
                externo.id(),
                externo.title(),
                externo.description(),
                externo.category(),
                externo.brand(),
                externo.price(),
                externo.discountPercentage(),
                externo.stock(),
                externo.rating(),
                externo.thumbnail()
        );
    }
}
