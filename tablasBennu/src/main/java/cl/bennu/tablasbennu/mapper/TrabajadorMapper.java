package cl.bennu.tablasbennu.mapper;

import cl.bennu.tablasbennu.model.Trabajador;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TrabajadorMapper {

    @Select("""
            SELECT id_trabajador, id_sucursal, dni, nombres, apellidos, puesto
            FROM trabajadores ORDER BY id_trabajador
            """)
    @ResultMap("cl.bennu.tablasbennu.mapper.ResultMaps.trabajadorMap")
    List<Trabajador> leerTodos();

    @Select("""
            SELECT id_trabajador, id_sucursal, dni, nombres, apellidos, puesto
            FROM trabajadores
            WHERE LOWER(nombres) LIKE LOWER(CONCAT('%', #{nombre}, '%'))
            ORDER BY id_trabajador
            """)
    @ResultMap("cl.bennu.tablasbennu.mapper.ResultMaps.trabajadorMap")
    List<Trabajador> buscarPorNombre(@Param("nombre") String nombre);

    @Select("""
            SELECT id_trabajador, id_sucursal, dni, nombres, apellidos, puesto
            FROM trabajadores WHERE dni = #{dni}
            """)
    @ResultMap("cl.bennu.tablasbennu.mapper.ResultMaps.trabajadorMap")
    Trabajador buscarPorDni(@Param("dni") String dni);

    @Insert("""
            INSERT INTO trabajadores (id_sucursal, dni, nombres, apellidos, puesto)
            VALUES (#{idSucursal}, #{dni}, #{nombres}, #{apellidos}, #{puesto})
            """)
    @Options(useGeneratedKeys = false)
    int crearTrabajador(@Param("idSucursal") int idSucursal,
                        @Param("dni") String dni,
                        @Param("nombres") String nombres,
                        @Param("apellidos") String apellidos,
                        @Param("puesto") String puesto);

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

    @Update("UPDATE trabajadores SET puesto = #{puesto} WHERE dni = #{dni}")
    int actualizarPuesto(@Param("dni") String dni, @Param("puesto") String puesto);

    @Delete("DELETE FROM trabajadores WHERE dni = #{dni}")
    int eliminarTrabajador(@Param("dni") String dni);
}
