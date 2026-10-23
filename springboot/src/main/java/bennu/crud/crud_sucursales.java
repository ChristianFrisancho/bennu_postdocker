package bennu.crud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sucursales")
public class crud_sucursales {

    private final sucursales mapper;

    public crud_sucursales(sucursales mapper) {
        this.mapper = mapper;
    }

    @GetMapping
    public List<Map<String, Object>> listarSucursales() {
        return mapper.listarSucursales();
    }

    @GetMapping("/{idSucursal}")
    public Map<String, Object> buscarSucursalPorId(@PathVariable int idSucursal) {
        Map<String, Object> sucursal = mapper.buscarPorId(idSucursal);
        if (sucursal == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
        return sucursal;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> crearSucursal(@Valid @RequestBody DatosSucursal datos) {
        Map<String, Object> nuevaSucursal = new HashMap<>();
        nuevaSucursal.put("id_pais", datos.idPais());
        nuevaSucursal.put("nombre", datos.nombre());
        nuevaSucursal.put("hora", datos.hora());

        int filasInsertadas = mapper.crearSucursal(nuevaSucursal);
        Object idGenerado = nuevaSucursal.get("id_sucursal");
        if (filasInsertadas == 0 || !(idGenerado instanceof Number idSucursal)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo recuperar la sucursal creada");
        }
        return buscarSucursalPorId(idSucursal.intValue());
    }

    @PutMapping("/{idSucursal}")
    public Map<String, Object> actualizarSucursal(@PathVariable int idSucursal,
                                                  @Valid @RequestBody DatosSucursal datos) {
        int filasActualizadas = mapper.actualizarSucursal(idSucursal, datos.idPais(),
                datos.nombre(), datos.hora());
        if (filasActualizadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
        return buscarSucursalPorId(idSucursal);
    }

    @PatchMapping("/{idSucursal}")
    @Transactional
    public Map<String, Object> actualizarParcialmenteSucursal(
            @PathVariable int idSucursal,
            @Valid @RequestBody DatosSucursalParcial datos) {
        if (datos.idPais() == null && datos.nombre() == null && datos.hora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe indicar al menos un campo para actualizar");
        }
        if (datos.nombre() != null && datos.nombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre enviado no puede estar vacío");
        }
        if (datos.hora() != null && datos.hora().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La hora enviada no puede estar vacía");
        }

        if (mapper.actualizarParcialmente(idSucursal, datos.idPais(), datos.nombre(), datos.hora()) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
        return buscarSucursalPorId(idSucursal);
    }

    @DeleteMapping("/{idSucursal}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarSucursal(@PathVariable int idSucursal) {
        if (mapper.eliminarSucursal(idSucursal) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada");
        }
    }

    public record DatosSucursal(
            @NotNull @Positive Integer idPais,
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Size(max = 50) String hora) {
    }

    public record DatosSucursalParcial(
            @Positive Integer idPais,
            @Size(max = 100) String nombre,
            @Size(max = 50) String hora) {
    }
}
