
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

import java.util.List;
import java.util.Map;

    public record ClaseDatosTrabajador(
            @NotNull @Positive Integer idSucursal,
            @NotBlank @Size(max = 20) String dni,
            @NotBlank @Size(max = 100) String nombres,
            @NotBlank @Size(max = 100) String apellidos,
            @NotBlank @Size(max = 80) String puesto) {
    }