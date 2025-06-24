package com.dataracy.user.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/dev-api/usertest")
    public String testLog() {
        return "OK!";
    }
}

