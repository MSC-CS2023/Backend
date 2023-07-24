package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.entity.Password;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.entity.UserType;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

import java.util.Date;

@RestController
public class LoginController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerPasswordMapper customerPasswordMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServiceProviderPasswordMapper providerPasswordMapper;

    private String email;
    private String password;
    private BaseMapper userMapper;
    private BaseMapper passwordMapper;

    @GetMapping("/customer_login")
    public Result customerLogin(String email, String password){
        this.email = email;
        this.password = password;
        this.userMapper = customerMapper;
        this.passwordMapper = customerPasswordMapper;
        return userLogin();
    }

    @GetMapping("/service_provider_login")
    public Result serviceProviderLogin(String email, String password){
        this.email = email;
        this.password = password;
        this.userMapper = providerMapper;
        this.passwordMapper = providerPasswordMapper;
        return userLogin();
    }

    private Result userLogin(){
        if (email == null){
            return Result.error().setMessage("Email is null");
        }
        if (password == null){
            return Result.error().setMessage("Password is null");
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        User user = (User) userMapper.selectOne(wrapper);
        if (user == null){
            return Result.error().setMessage("Email cannot be found.");
        }
        Integer id = user.getId();
        Password userPassword = (Password) passwordMapper.selectById(id);
        if (userPassword == null) {
            return Result.error().setMessage("No password data.");
        } else if (userPassword.getPassword().compareTo(password) != 0){
            return Result.error().setMessage("Password is wrong.");
        }
        String token = JwtUtils.generateToken(user, UserType.CUSTOMER);
        Claims claims = JwtUtils.getClaimsByToken(token);
        String exp = String.valueOf(claims.getExpiration().getTime());
        return Result.ok().data("user", user).data("token", token).data("user", user).data("exp", exp);
    }
}
