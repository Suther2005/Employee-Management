package com.ems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles login / logout page rendering.
 * The actual authentication is processed by Spring Security.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
}
