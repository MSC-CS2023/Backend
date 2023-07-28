package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.*;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

@RestController
public class RegisterController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerPasswordMapper customerPasswordMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServiceProviderPasswordMapper providerPasswordMapper;

    private User user;
    private Password userPassword;
    private BaseMapper userMapper;
    private BaseMapper passwordMapper;
    private int usertype;

    @PutMapping("/customer_register")
    public Result customerRegister(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "") String address,
            @RequestParam(required = false, defaultValue = "") String tel){
        this.user = new Customer();
        this.userPassword = new CustomerPassword();
        init(email, username, password, address, tel);
        this.userMapper = customerMapper;
        this.passwordMapper = customerPasswordMapper;
        this.usertype = UserType.CUSTOMER;
        return userRegister();
    }

    @PutMapping("/service_provider_register")
    public Result serviceRegister(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false, defaultValue = "") String address,
            @RequestParam(required = false, defaultValue = "") String tel){
        this.user = new ServiceProvider();
        this.userPassword = new ServiceProviderPassword();
        init(email, username, password, address, tel);
        this.userMapper = providerMapper;
        this.passwordMapper = providerPasswordMapper;
        this.usertype = UserType.PROVIDER;
        return userRegister();
    }

    private void init(String email, String username, String password, String address, String tel){
        user.setEmail(email);
        user.setUsername(username);
        user.setAddress(address);
        user.setTel(tel);
        userPassword.setPassword(password);
    }

    private Result userRegister() {
        if (user.getEmail().isEmpty()){
            return Result.error().setMessage("Email is empty");
        }
        if (user.getUsername().isEmpty()){
            return Result.error().setMessage("Username is null");
        }
        if (userPassword.getPassword().isEmpty()){
            return Result.error().setMessage("Password is null");
        }
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        wrapper.eq("email", user.getEmail());
        if (userMapper.selectOne(wrapper) != null){
            return Result.error().setMessage("The email has existed.");
        }
        userMapper.insert(user);
        long id = user.getId();
        userPassword.setId(id);
        passwordMapper.insert(userPassword);

        String token = JwtUtils.generateToken(user, usertype);
        Claims claims = JwtUtils.getClaimsByToken(token);
        String exp = String.valueOf(claims.getExpiration().getTime());
        return Result.ok().data("user", user).data("token", token).data("user", user).data("exp", exp);
    }
}
