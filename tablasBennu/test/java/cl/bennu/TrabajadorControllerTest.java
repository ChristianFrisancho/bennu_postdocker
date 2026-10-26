package cl.bennu;

import cl.bennu.tablasbennu.App;
import cl.bennu.tablasbennu.controller.TrabajadorController;
import cl.bennu.tablasbennu.mapper.TrabajadorMapper;
import cl.bennu.tablasbennu.model.Trabajador;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrabajadorController.class)
@ContextConfiguration(classes = App.class)
class TrabajadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrabajadorMapper mapper;

    @Test
    void existeTrabajador() throws Exception {
        Trabajador datos = new Trabajador();
        datos.setDni("12345678");
        datos.setNombres("Ana");
        datos.setApellidos("Perez");
        given(mapper.buscarPorDni("12345678")).willReturn(datos);

        mockMvc.perform(get("/api/trabajadores/{dni}", "12345678"))
                .andExpect(status().isOk());
    }

    @Test
    void noExisteTrabajador() throws Exception {
        given(mapper.buscarPorDni("39042018")).willReturn(null);

        mockMvc.perform(get("/api/trabajadores/{dni}", "39042018"))
                .andExpect(status().isNotFound());
    }
}
