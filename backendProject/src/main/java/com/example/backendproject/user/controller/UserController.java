package com.example.backendproject.user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    //docker 서버 :8080로 지정된 서버가 있으면 실행안됨!
    @Value("${PROJECT_NAME:web server}")
    private String instansName;

    @GetMapping
    public String test(){
        return instansName;
    }
}
