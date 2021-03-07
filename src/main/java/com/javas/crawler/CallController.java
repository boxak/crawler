package com.javas.crawler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CallController {
    @GetMapping("/test")
    public String test() {
        return "project working!!!!";
    }
}
