package cl.bennu.tablasbennu.controller.base;

import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected static ResponseEntity<Void> ok() {
        return ResponseEntity.ok().build();
    }

    protected static <T> ResponseEntity<T> ok(T datos) {
        return ResponseEntity.ok(datos);
    }
}
