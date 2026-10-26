package cl.bennu.tablasbennu.mapper;

import cl.bennu.tablasbennu.model.Sucursal;
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
public interface SucursalMapper {

    @Select("SELECT id_sucursal, id_pais, nombre, hora FROM sucursales ORDER BY id_sucursal")
    @ResultMap("cl.bennu.tablasbennu.mapper.ResultMaps.sucursalMap")
    List<Sucursal> listarSucursales();

    @Select("SELECT id_sucursal, id_pais, nombre, hora FROM sucursales WHERE id_sucursal = #{idSucursal}")
    @ResultMap("cl.bennu.tablasbennu.mapper.ResultMaps.sucursalMap")
    Sucursal buscarPorId(@Param("idSucursal") int idSucursal);

    @Insert("INSERT INTO sucursales (id_pais, nombre, hora) VALUES (#{idPais}, #{nombre}, #{hora})")
    @Options(useGeneratedKeys = false)
    int crearSucursal(@Param("idPais") int idPais,
                      @Param("nombre") String nombre,
                      @Param("hora") String hora);

    // Recupera el ID generado en la misma conexion del INSERT.
    @Select("SELECT currval(pg_get_serial_sequence('sucursales', 'id_sucursal'))")
    int obtenerIdSucursalCreada();

    @Update("""
            UPDATE sucursales SET id_pais = #{idPais}, nombre = #{nombre}, hora = #{hora}
            WHERE id_sucursal = #{idSucursal}
            """)
    int actualizarSucursal(@Param("idSucursal") int idSucursal,
                           @Param("idPais") int idPais,
                           @Param("nombre") String nombre,
                           @Param("hora") String hora);

    @Update("""
            <script>
            UPDATE sucursales
            <set>
                <if test="idPais != null">id_pais = #{idPais},</if>
                <if test="nombre != null">nombre = #{nombre},</if>
                <if test="hora != null">hora = #{hora},</if>
            </set>
            WHERE id_sucursal = #{idSucursal}
            </script>
            """)
    int actualizarParcialmente(@Param("idSucursal") int idSucursal,
                              @Param("idPais") Integer idPais,
                              @Param("nombre") String nombre,
                              @Param("hora") String hora);

    @Delete("DELETE FROM sucursales WHERE id_sucursal = #{idSucursal}")
    int eliminarSucursal(@Param("idSucursal") int idSucursal);
}
