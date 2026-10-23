package bennu.crud;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface trabajadores {

    @Insert("""
            INSERT INTO trabajadores (id_sucursal, dni, nombres, apellidos, puesto)
            VALUES (#{idSucursal}, #{dni}, #{nombres}, #{apellidos}, #{puesto})
            """)
    int crearTrabajador(@Param("idSucursal") int idSucursal,
                        @Param("dni") String dni,
                        @Param("nombres") String nombres,
                        @Param("apellidos") String apellidos,
                        @Param("puesto") String puesto);

    @Select("""
            SELECT id_trabajador, id_sucursal, dni, nombres, apellidos, puesto
            FROM trabajadores ORDER BY id_trabajador
            """)
    List<Map<String, Object>> leerTodos();

    @Select("""
            SELECT id_trabajador, id_sucursal, dni, nombres, apellidos, puesto
            FROM trabajadores
            WHERE LOWER(nombres) LIKE LOWER(CONCAT('%', #{nombre}, '%'))
            ORDER BY id_trabajador
            """)
    List<Map<String, Object>> buscarPorNombre(@Param("nombre") String nombre);

    @Select("""
            SELECT id_trabajador, id_sucursal, dni, nombres, apellidos, puesto
            FROM trabajadores WHERE dni = #{dni}
            """)
    Map<String, Object> buscarPorDni(@Param("dni") String dni);

    @Update("""
            UPDATE trabajadores
            SET id_sucursal = #{idSucursal}, dni = #{dni},
                nombres = #{nombres}, apellidos = #{apellidos}, puesto = #{puesto}
            WHERE dni = #{dniActual}
            """)
    int actualizarTrabajador(@Param("dniActual") String dniActual,
                             @Param("idSucursal") int idSucursal,
                             @Param("dni") String dni,
                             @Param("nombres") String nombres,
                             @Param("apellidos") String apellidos,
                             @Param("puesto") String puesto);

    @Update("UPDATE trabajadores SET puesto = #{nuevoPuesto} WHERE dni = #{dni}")
    int actualizarPuesto(@Param("dni") String dni, @Param("nuevoPuesto") String nuevoPuesto);

    @Delete("DELETE FROM trabajadores WHERE dni = #{dni}")
    int eliminarTrabajador(@Param("dni") String dni);
}
