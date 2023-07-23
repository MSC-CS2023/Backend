package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.entity.Password;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.entity.UserType;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

@RestController
public class LoginController {
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerPasswordMapper customerPasswordMapper;

    @GetMapping("/customer_login")
    public Result customerLogin(String email, String password){
        if (email == null){
            return Result.error().setMessage("Email is null");
        }
        if (password == null){
            return Result.error().setMessage("Password is null");
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        User user = customerMapper.selectOne(wrapper);
        if (user == null){
            return Result.error().setMessage("Email cannot be found.");
        }
        Integer id = user.getId();
        Password userPassword = customerPasswordMapper.selectById(id);
        if (userPassword == null) {
            return Result.error().setMessage("No password data.");
        } else if (userPassword.getPassword().compareTo(password) != 0){
            return Result.error().setMessage("Password is wrong.");
        }
        String token = JwtUtils.generateToken(user, UserType.CUSTOMER);
        return Result.ok().data("user", user).data("token", token).data("user", user);
    }
}
