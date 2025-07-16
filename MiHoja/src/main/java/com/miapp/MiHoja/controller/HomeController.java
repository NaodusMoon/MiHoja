package com.miapp.MiHoja.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/consultar"; // mejor redireccionar al endpoint
    }
}
