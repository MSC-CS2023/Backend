package uk.gigbookingapp.backend.controller;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@MapperScan("uk.gigbookingapp.backend.mapper")
public class MainController {
    @GetMapping("hello")
    public String hello(){
        return "hello world123";
    }
}
