package cl.bennu;

import cl.bennu.tablasbennu.App;
import cl.bennu.tablasbennu.controller.SucursalController;
import cl.bennu.tablasbennu.mapper.SucursalMapper;
import cl.bennu.tablasbennu.model.Sucursal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SucursalController.class)
@ContextConfiguration(classes = App.class)
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SucursalMapper mapper;

    @Test
    void existeSucursal() throws Exception {
        Sucursal datos = new Sucursal();
        datos.setIdSucursal(1);
        datos.setIdPais(1);
        datos.setNombre("Centro");
        datos.setHora("09:00");
        given(mapper.buscarPorId(1)).willReturn(datos);

        mockMvc.perform(get("/api/sucursales/{idSucursal}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void noExisteSucursal() throws Exception {
        given(mapper.buscarPorId(389)).willReturn(null);

        mockMvc.perform(get("/api/sucursales/{idSucursal}", 389))
                .andExpect(status().isNotFound());
    }
}
