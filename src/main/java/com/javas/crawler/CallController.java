package com.javas.crawler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallController {
    @GetMapping("/test")
    public String test() {
        return "project working!!!!";
    }
}
