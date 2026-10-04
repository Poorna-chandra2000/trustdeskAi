package com.demo.trustdeskAi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller//not this is important
public class HomeController {

    /**
     * Forwards the root URL ("/") to index.html stored in src/main/resources/static/
     */
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}