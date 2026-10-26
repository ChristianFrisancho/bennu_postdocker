package cl.bennu.tablasbennu.mapper;

import cl.bennu.tablasbennu.model.Trabajador;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrabajadorMapper {

    List<Trabajador> leerTodos();

    List<Trabajador> buscarPorNombre(@Param("nombre") String nombre);

    Trabajador buscarPorDni(@Param("dni") String dni);

    int crearTrabajador(@Param("idSucursal") int idSucursal,
                        @Param("dni") String dni,
                        @Param("nombres") String nombres,
                        @Param("apellidos") String apellidos,
                        @Param("puesto") String puesto);

    int actualizarTrabajador(@Param("dniActual") String dniActual,
                             @Param("idSucursal") int idSucursal,
                             @Param("dni") String dni,
                             @Param("nombres") String nombres,
                             @Param("apellidos") String apellidos,
                             @Param("puesto") String puesto);

    int actualizarPuesto(@Param("dni") String dni, @Param("puesto") String puesto);

    int eliminarTrabajador(@Param("dni") String dni);
}
