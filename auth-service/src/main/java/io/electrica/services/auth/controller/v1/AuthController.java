package io.electrica.services.auth.controller.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login() {
       return "Login Test";
    }
}
