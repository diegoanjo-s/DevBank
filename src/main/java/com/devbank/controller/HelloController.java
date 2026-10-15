package com.devbank.controller;

import com.devbank.dto.HelloResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, DevBank!";
    }

    @GetMapping("/hello/{nome}")
    public HelloResponse helloNome(@PathVariable String nome){
        return new HelloResponse("Hello, " + nome + "!", "DevBank", LocalDateTime.now() );
    }

}
