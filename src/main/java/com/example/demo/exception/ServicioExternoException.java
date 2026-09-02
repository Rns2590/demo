package com.example.demo.exception;

/**
 * Se lanza cuando falla la comunicación con un servicio externo (DummyJSON):
 * timeout, caída del servicio, o una respuesta de error que no sabemos
 * interpretar. Se traduce a un 5xx propio en {@link GlobalExceptionHandler}
 * en vez de dejar que el cliente vea la excepción cruda de RestClient.
 */
public class ServicioExternoException extends RuntimeException {
    public ServicioExternoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
