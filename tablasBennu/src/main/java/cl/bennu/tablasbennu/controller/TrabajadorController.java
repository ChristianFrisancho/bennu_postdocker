package cl.bennu.tablasbennu.controller;

import cl.bennu.tablasbennu.mapper.TrabajadorMapper;
import cl.bennu.tablasbennu.model.Trabajador;
import cl.bennu.tablasbennu.service.Validaciones;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/trabajadores")
public class TrabajadorController {

    private final TrabajadorMapper mapper;

    public TrabajadorController(TrabajadorMapper mapper) {
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Trabajador crearTrabajador(@RequestBody Trabajador datos) {
        Validaciones.validarTrabajador(datos);
        if (mapper.buscarPorDni(datos.getDni()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya existe en la base de datos");
        }
        int filasInsertadas = mapper.crearTrabajador(
                datos.getIdSucursal(), datos.getDni(), datos.getNombres(),
                datos.getApellidos(), datos.getPuesto());
        if (filasInsertadas != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo crear el trabajador");
        }
        return buscarTrabajadorPorDni(datos.getDni());
    }

    @PostMapping("/buscar")
    public Trabajador buscarTrabajadorPorDniEnBody(@RequestBody Trabajador datos) {
        return buscarTrabajadorPorDni(datos.getDni());
    }

    @GetMapping
    public List<Trabajador> leerTrabajadores() {
        return mapper.leerTodos();
    }

    @GetMapping(params = "nombre")
    public List<Trabajador> buscarTrabajadorPorNombre(@RequestParam @NotBlank @Size(max = 100) String nombre) {
        return mapper.buscarPorNombre(nombre);
    }

    @GetMapping("/{dni}")
    public Trabajador buscarTrabajadorPorDni(@PathVariable @NotBlank @Size(max = 20) String dni) {
        Validaciones.validarTexto(dni, 20, "El DNI");
        Trabajador trabajador = mapper.buscarPorDni(dni);
        if (trabajador == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
        return trabajador;
    }

    @PutMapping("/{dni}")
    @Transactional
    public Trabajador actualizarTrabajador(@PathVariable @NotBlank @Size(max = 20) String dni,
                                           @RequestBody Trabajador datos) {
        Validaciones.validarTrabajador(datos);
        Trabajador actual = buscarTrabajadorPorDni(dni);
        Trabajador existente = mapper.buscarPorDni(datos.getDni());
        if (existente != null && existente.getIdTrabajador() != actual.getIdTrabajador()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya existe en la base de datos");
        }
        int filasActualizadas = mapper.actualizarTrabajador(
                dni, datos.getIdSucursal(), datos.getDni(), datos.getNombres(),
                datos.getApellidos(), datos.getPuesto());
        if (filasActualizadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
        return buscarTrabajadorPorDni(datos.getDni());
    }

    @PatchMapping("/{dni}/puesto")
    @Transactional
    public Trabajador actualizarPuesto(@PathVariable @NotBlank @Size(max = 20) String dni,
                                       @RequestBody Trabajador datos) {
        Validaciones.validarTexto(datos.getPuesto(), 80, "El puesto");
        int filasActualizadas = mapper.actualizarPuesto(dni, datos.getPuesto());
        if (filasActualizadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
        return buscarTrabajadorPorDni(dni);
    }

    @DeleteMapping("/{dni}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTrabajador(@PathVariable @NotBlank @Size(max = 20) String dni) {
        int filasEliminadas = mapper.eliminarTrabajador(dni);
        if (filasEliminadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail manejarError(ResponseStatusException exception) {
        return exception.getBody();
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail manejarErrorBaseDatos(DataAccessException exception) {
        if (exception instanceof DuplicateKeyException) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Ya existe un registro con ese valor unico");
        }
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
