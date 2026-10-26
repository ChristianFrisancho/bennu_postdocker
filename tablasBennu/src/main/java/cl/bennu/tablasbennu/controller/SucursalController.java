package cl.bennu.tablasbennu.controller;

import cl.bennu.tablasbennu.mapper.SucursalMapper;
import cl.bennu.tablasbennu.model.Sucursal;
import cl.bennu.tablasbennu.service.Validaciones;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    private final SucursalMapper mapper;

    public SucursalController(SucursalMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public List<Sucursal> listarSucursales() {
        return mapper.listarSucursales();
    }

    @GetMapping("/{idSucursal}")
    public Sucursal buscarSucursalPorId(@PathVariable @Positive int idSucursal) {
        Sucursal sucursal = mapper.buscarPorId(idSucursal);
        if (sucursal == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
        return sucursal;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Sucursal crearSucursal(@RequestBody Sucursal datos) {
        Validaciones.validarSucursal(datos, false);
        int filasInsertadas = mapper.crearSucursal(
                datos.getIdPais(), datos.getNombre(), datos.getHora());
        if (filasInsertadas != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo crear la sucursal");
        }
        int idSucursal = mapper.obtenerIdSucursalCreada();
        return buscarSucursalPorId(idSucursal);
    }

    @PutMapping("/{idSucursal}")
    @Transactional
    public Sucursal actualizarSucursal(@PathVariable @Positive int idSucursal,
                                       @RequestBody Sucursal datos) {
        Validaciones.validarSucursal(datos, false);
        int filasActualizadas = mapper.actualizarSucursal(
                idSucursal, datos.getIdPais(), datos.getNombre(), datos.getHora());
        if (filasActualizadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
        return buscarSucursalPorId(idSucursal);
    }

    @PatchMapping("/{idSucursal}")
    @Transactional
    public Sucursal actualizarParcialmenteSucursal(@PathVariable @Positive int idSucursal,
                                                   @RequestBody Sucursal datos) {
        Validaciones.validarSucursal(datos, true);
        int filasActualizadas = mapper.actualizarParcialmente(
                idSucursal, datos.getIdPais(), datos.getNombre(), datos.getHora());
        if (filasActualizadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
        return buscarSucursalPorId(idSucursal);
    }

    @DeleteMapping("/{idSucursal}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarSucursal(@PathVariable @Positive int idSucursal) {
        int filasEliminadas = mapper.eliminarSucursal(idSucursal);
        if (filasEliminadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail manejarError(ResponseStatusException exception) {
        return exception.getBody();
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail manejarErrorBaseDatos(DataAccessException exception) {
        if (exception instanceof DataIntegrityViolationException) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    "La operacion entra en conflicto con las relaciones o restricciones de la base de datos");
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo completar la operacion en la base de datos");
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, HandlerMethodValidationException.class,
            MethodArgumentTypeMismatchException.class})
    public ProblemDetail manejarEntradaInvalida(Exception exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Revise el JSON y los parametros enviados");
    }
}
