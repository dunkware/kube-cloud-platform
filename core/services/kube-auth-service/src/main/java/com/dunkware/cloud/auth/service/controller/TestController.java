package com.dunkware.cloud.auth.service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @PreAuthorize("hasAuthority('user.read')")
    @RequestMapping("/ping")
    public String ping() {
        return "pong";
    }
}
