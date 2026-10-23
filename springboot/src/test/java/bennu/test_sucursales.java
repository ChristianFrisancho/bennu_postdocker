package bennu.crud;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(crud_sucursales.class)
class SucursalesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private sucursales mapper;

    @Test
    void existesucursal() throws Exception {
        given(mapper.buscarPorId(1))
                .willReturn(Map.of(
                        "id_sucursal", 1,
                        "id_pais", 1,
                        "nombre", "Sucursal Centro",
                        "hora", "09:00"
                ));

        mockMvc.perform(get("/api/sucursales/{idSucursal}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void noexistesucursal() throws Exception {
        given(mapper.buscarPorId(389))
                .willReturn(null);

        mockMvc.perform(get("/api/sucursales/{idSucursal}", 389))
                .andExpect(status().isNotFound());
    }
}
