package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.entity.Password;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

import java.util.Arrays;

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
    private int usertype;


    @PostMapping("/customer_login")
    public Result customerLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request){
        System.out.println();
        System.out.println(Arrays.toString(request.getParameterValues("email")));
        this.email = email;
        this.password = password;
        this.userMapper = customerMapper;
        this.passwordMapper = customerPasswordMapper;
        this.usertype = UserType.CUSTOMER;
        return userLogin();
    }

    @PostMapping("/service_provider_login")
    public Result serviceProviderLogin(
            @RequestParam String email,
            @RequestParam String password){
        this.email = email;
        this.password = password;
        this.userMapper = providerMapper;
        this.passwordMapper = providerPasswordMapper;
        this.usertype = UserType.PROVIDER;
        return userLogin();
    }

    private Result userLogin() {
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        User user = (User) userMapper.selectOne(wrapper);

        if (user == null){
            return Result.error().setMessage("Email cannot be found.");
        }
        Long id = user.getId();
        Password userPassword = (Password) passwordMapper.selectById(id);
        if (userPassword == null) {
            return Result.error().setMessage("No password data.");
        } else if (userPassword.getPassword().compareTo(password) != 0){
            return Result.error().setMessage("Password is wrong.");
        }
        String token = JwtUtils.generateToken(user, usertype);
        Claims claims = JwtUtils.getClaimsByToken(token);
        Long exp = claims.getExpiration().getTime();
        return Result.ok().data("user", user).data("token", token).data("exp", exp);
    }



}
