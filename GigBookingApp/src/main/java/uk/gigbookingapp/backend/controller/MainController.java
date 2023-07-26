package uk.gigbookingapp.backend.controller;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.utils.Result;

@RestController
public class MainController {
    @GetMapping("hello")
    public String hello(){
        return "hello world123";
    }

//    @PostMapping("/find_password")
//    public Result findPassword(String email){
//
//    }
}
