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

@WebMvcTest(crud_trabajadores.class)
class CrudTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private trabajadores mapper;

    @Test
    void existeusuario() throws Exception {
        given(mapper.buscarPorDni("12345678"))
                .willReturn(Map.of(
                        "dni", "12345678",
                        "nombres", "Ana",
                        "apellidos", "Perez"
                ));

        mockMvc.perform(get("/api/trabajadores/{dni}", "12345678"))
                .andExpect(status().isOk());
    }

    @Test
    void noexisteusuario() throws Exception {
        given(mapper.buscarPorDni("99999999"))
                .willReturn(null);

        mockMvc.perform(get("/api/trabajadores/{dni}", "99999999"))
                .andExpect(status().isNotFound());
    }
}