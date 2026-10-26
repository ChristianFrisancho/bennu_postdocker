package cl.bennu.tablasbennu.controller;

import cl.bennu.tablasbennu.constants.Constants;
import cl.bennu.tablasbennu.controller.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.baseApi + "/common")
public class CommonController extends BaseController {

    @GetMapping("/health-check")
    public ResponseEntity<Void> healthcheck() {
        return ok();
    }
}
