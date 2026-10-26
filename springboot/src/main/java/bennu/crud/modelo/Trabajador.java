package bennu.crud.modelo;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Data public class Trabajador{
    private int idTrabajador;
    private int idSucursal;
    private String dni;
    private String nombres;
    private String apellidos;
    private String puesto;

}