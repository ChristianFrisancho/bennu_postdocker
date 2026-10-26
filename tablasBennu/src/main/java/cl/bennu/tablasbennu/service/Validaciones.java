package cl.bennu.tablasbennu.service;

import cl.bennu.tablasbennu.model.Sucursal;
import cl.bennu.tablasbennu.model.Trabajador;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Validaciones {

    private Validaciones() {
    }

    public static void validarTrabajador(Trabajador datos) {
        if (datos.getIdSucursal() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idSucursal debe ser positivo");
        }
        validarTexto(datos.getDni(), 20, "El DNI");
        validarTexto(datos.getNombres(), 100, "Los nombres");
        validarTexto(datos.getApellidos(), 100, "Los apellidos");
        validarTexto(datos.getPuesto(), 80, "El puesto");
    }

    public static void validarSucursal(Sucursal datos, boolean parcial) {
        if (parcial && datos.getIdPais() == null && datos.getNombre() == null && datos.getHora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar al menos un campo para actualizar");
        }
        if ((!parcial || datos.getIdPais() != null) && (datos.getIdPais() == null || datos.getIdPais() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idPais debe ser positivo");
        }
        if (!parcial || datos.getNombre() != null) {
            validarTexto(datos.getNombre(), 100, "El nombre");
        }
        if (!parcial || datos.getHora() != null) {
            validarTexto(datos.getHora(), 50, "La hora");
        }
    }

    public static void validarTexto(String texto, int maximo, String campo) {
        if (texto == null || texto.isBlank() || texto.length() > maximo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    campo + " es obligatorio y admite hasta " + maximo + " caracteres");
        }
    }
}
