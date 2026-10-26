package bennu.crud;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import bennu.crud.modelo.Trabajador;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Data;
import lombok.ToString;

@RestController
@RequestMapping("/api/trabajadores")
public class crud_trabajadores {

    private final trabajadores mapper;

    public crud_trabajadores(trabajadores mapper) {
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    
    
    public Map<String, Object> crearTrabajador(@Valid @RequestBody Trabajador datos) {
        if (mapper.buscarPorDni(datos.getDni())!= null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El dni ya existe en la base de datos");
        }
        int filasInsertadas = mapper.crearTrabajador(datos.getIdSucursal(), datos.getDni(), datos.getNombres(), datos.getApellidos(), datos.getPuesto());
        System.out.println("Trabajador insertado : " + filasInsertadas);
        return buscarTrabajadorPorDni(datos.getDni());
        
    }

    @PostMapping("/buscar")
    public Map<String, Object> buscarTrabajadorPorDniEnBody(@RequestBody Map<String, String> datos) {
        String dni = datos.get("dni");
        if (dni == null || dni.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El DNI es obligatorio");
        }
        if (dni == mapper.buscarPorDni(dni).get("dni")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El dni ya existe en la base de datos");
        }
        return buscarTrabajadorPorDni(dni);
    }

    @GetMapping
    public List<Map<String, Object>> leerTrabajadores() {
        List<Map<String, Object>> lista = mapper.leerTodos();
        System.out.println("\n Trabajadores Registrados: \n");
        for (Map<String, Object> fila : lista) {
            System.out.printf("ID: %s | DNI: %s | Nombre: %s %s | Puesto: %s%n",
                    fila.get("id_trabajador"), fila.get("dni"), fila.get("nombres"),
                    fila.get("apellidos"), fila.get("puesto"));
        }
        System.out.println("\n");
        return lista;
    }

    @GetMapping(params = "nombre")
    public List<Map<String, Object>> buscarTrabajadorPorNombre(@RequestParam String nombre) {
        List<Map<String, Object>> lista = mapper.buscarPorNombre(nombre);
        System.out.println("\n Busqueda por nombre: \n");
        if (lista.isEmpty()) {
            System.out.println("No se encontro un trabajador con ese nombre: " + nombre);
        } else {
            for (Map<String, Object> fila : lista) {
                System.out.printf("ID: %s | DNI: %s | Nombre: %s %s | Puesto: %s%n",
                        fila.get("id_trabajador"), fila.get("dni"), fila.get("nombres"),
                        fila.get("apellidos"), fila.get("puesto"));
            }
        }
        System.out.println("\n");
        return lista;
    }

    @GetMapping("/{dni}")
    public Map<String, Object> buscarTrabajadorPorDni(@PathVariable String dni) {
        Map<String, Object> trabajador = mapper.buscarPorDni(dni);
        if (trabajador == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
        return trabajador;
    }

    @PutMapping("/{dni}")
    @Transactional
    public Map<String, Object> actualizarTrabajador(@PathVariable String dni,
                                                   @Valid @RequestBody Trabajador datos) {
        if (mapper.actualizarTrabajador(dni, datos.getIdSucursal(), datos.getDni(),
                datos.getNombres(), datos.getApellidos(), datos.getPuesto()) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
        return buscarTrabajadorPorDni(datos.getDni());
    }

    @PatchMapping("/{dni}/puesto")
    @Transactional
    public Map<String, Object> actualizarPuesto(@PathVariable String dni,
                                               @RequestBody Map<String, String> datos) {
        String nuevoPuesto = datos.get("puesto");
        if (nuevoPuesto == null || nuevoPuesto.isBlank() || nuevoPuesto.length() > 80) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El puesto es obligatorio y admite hasta 80 caracteres");
        }
        int filasActualizadas = mapper.actualizarPuesto(dni, nuevoPuesto);
        System.out.println("Puesto del trabajador actualizado: " + filasActualizadas);
        if (filasActualizadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
        return buscarTrabajadorPorDni(dni);
    }

    @DeleteMapping("/{dni}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarTrabajador(@PathVariable String dni) {
        int filasEliminadas = mapper.eliminarTrabajador(dni);
        System.out.println("Trabajador eliminado: " + filasEliminadas);
        if (filasEliminadas == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado");
        }
    }


}
