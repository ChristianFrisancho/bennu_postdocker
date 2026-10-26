package cl.bennu.tablasbennu.config;

import cl.bennu.tablasbennu.constants.Constants;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class RestConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(@Value("${app.cors.origins}") List<String> origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Accept", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        List<String> rutas = List.of(
                "/sucursales", "/sucursales/{idSucursal}",
                "/trabajadores", "/trabajadores/{dni}", "/trabajadores/{dni}/puesto",
                "/common/health-check");
        for (String ruta : rutas) {
            source.registerCorsConfiguration(Constants.baseApi + ruta, config);
        }
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
