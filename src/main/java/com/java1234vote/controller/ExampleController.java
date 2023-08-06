package com.java1234vote.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
public class ExampleController {
    @GetMapping("{name}")
    public String test(@PathVariable("name") String name) {
        System.out.println("Hello, " + name + "!");
        return "Hello, " + name + "!";
    }
}