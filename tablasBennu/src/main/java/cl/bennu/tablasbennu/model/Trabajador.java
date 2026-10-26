package cl.bennu.tablasbennu.model;

import lombok.Data;

@Data
public class Trabajador {
    private int idTrabajador;
    private int idSucursal;
    private String dni;
    private String nombres;
    private String apellidos;
    private String puesto;
}
