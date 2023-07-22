package uk.gigbookingapp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.Password;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.entity.UserType;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerPasswordMapper customerPasswordMapper;

    @PostMapping
    public Result customerLogin(String id, String password){
        User user = customerMapper.selectById(id);
        Password userPassword = customerPasswordMapper.selectById(id);
        if (user == null){
            return Result.error().setMessage("ID does not exist.");
        } else if (userPassword == null) {
            return Result.error().setMessage("Password does not exist.");
        } else if (userPassword.getPassword().compareTo(password) != 0){
            return Result.error().setMessage("Password is wrong.");
        }
        String token = JwtUtils.generateToken(user, UserType.CUSTOMER);
        return Result.ok().data("user", user).data("token", token).data("user", user);
    }
}
