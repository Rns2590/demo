package com.example.demo.client.dummyjson;

import com.example.demo.exception.RecursoNoEncontradoException;
import com.example.demo.exception.ServicioExternoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Única clase del proyecto que conoce el contrato JSON de DummyJSON (campos
 * como "title" o "thumbnail"). Todo lo demás — service, controller — trabaja
 * siempre con com.example.demo.dto.producto.ProductoDTO.
 *
 * También es la única clase que sabe traducir una falla de red/HTTP externa
 * a una excepción propia del dominio (RecursoNoEncontradoException,
 * ServicioExternoException), que después maneja GlobalExceptionHandler.
 */
@Component
public class DummyJsonClient {

    private final RestClient restClient;

    public DummyJsonClient(RestClient dummyJsonRestClient) {
        this.restClient = dummyJsonRestClient;
    }

    public DummyJsonProductosResponse listar(int limit, int skip) {
        try {
            return restClient.get()
                    .uri("/products?limit={limit}&skip={skip}", limit, skip)
                    .retrieve()
                    .body(DummyJsonProductosResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ServicioExternoException("DummyJSON respondió con error al listar productos", e);
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("No se pudo contactar a DummyJSON (timeout o caída del servicio)", e);
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error inesperado al consumir DummyJSON", e);
        }
    }

    public DummyJsonProducto obtenerPorId(Long id) {
        try {
            return restClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .body(DummyJsonProducto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("No existe el producto con id " + id);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ServicioExternoException("DummyJSON respondió con error al buscar el producto " + id, e);
        } catch (ResourceAccessException e) {
            throw new ServicioExternoException("No se pudo contactar a DummyJSON (timeout o caída del servicio)", e);
        } catch (RestClientException e) {
            throw new ServicioExternoException("Error inesperado al consumir DummyJSON", e);
        }
    }
}
