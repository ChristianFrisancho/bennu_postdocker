package cl.bennu.tablasbennu.mapper;

import cl.bennu.tablasbennu.model.Sucursal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SucursalMapper {

    List<Sucursal> listarSucursales();

    Sucursal buscarPorId(@Param("idSucursal") int idSucursal);

    int crearSucursal(@Param("idPais") int idPais,
                      @Param("nombre") String nombre,
                      @Param("hora") String hora);

    int obtenerIdSucursalCreada();

    int actualizarSucursal(@Param("idSucursal") int idSucursal,
                           @Param("idPais") int idPais,
                           @Param("nombre") String nombre,
                           @Param("hora") String hora);

    int actualizarParcialmente(@Param("idSucursal") int idSucursal,
                              @Param("idPais") Integer idPais,
                              @Param("nombre") String nombre,
                              @Param("hora") String hora);

    int eliminarSucursal(@Param("idSucursal") int idSucursal);
}
