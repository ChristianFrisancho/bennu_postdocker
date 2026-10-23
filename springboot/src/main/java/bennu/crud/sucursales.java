package bennu.crud;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface sucursales {

    @Insert("""
            INSERT INTO sucursales (id_pais, nombre, hora)
            VALUES (#{id_pais}, #{nombre}, #{hora})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id_sucursal", keyColumn = "id_sucursal")
    int crearSucursal(Map<String, Object> sucursal);

    @Select("""
            SELECT id_sucursal, id_pais, nombre, hora
            FROM sucursales
            ORDER BY id_sucursal
            """)
    List<Map<String, Object>> listarSucursales();

    @Select("""
            SELECT id_sucursal, id_pais, nombre, hora
            FROM sucursales
            WHERE id_sucursal = #{idSucursal}
            """)
    Map<String, Object> buscarPorId(@Param("idSucursal") int idSucursal);

    @Update("""
            UPDATE sucursales
            SET id_pais = #{idPais}, nombre = #{nombre}, hora = #{hora}
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
