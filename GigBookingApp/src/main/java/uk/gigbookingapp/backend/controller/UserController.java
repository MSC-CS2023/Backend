package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.*;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping({"/customer", "/service_provider"})
public class UserController {
    @Autowired
    private CustomerPasswordMapper customerPasswordMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServiceProviderPasswordMapper providerPasswordMapper;

    private CurrentId currentId;
    private BaseMapper userMapper;
    private BaseMapper passwordMapper;
    private int id;


    @Autowired
    UserController(CurrentId currentId) {
        this.currentId = currentId;
    }

    // Every request method in this class should begin with init()
    private void init(){
        this.id = this.currentId.getId();
        this.userMapper = this.currentId.getUsertype() == UserType.CUSTOMER ? customerMapper : providerMapper;
        this.passwordMapper = this.currentId.getUsertype() == UserType.CUSTOMER ? customerPasswordMapper : providerPasswordMapper;
    }

    @GetMapping("/self_details")
    public Result getSelfDetails(){
        init();
        Customer customer = customerMapper.selectById(id);
        return Result.ok().data("user", customer);
    }

    @DeleteMapping("/delete_account")
    public Result deleteAccount(){;
        init();
        try {
            passwordMapper.deleteById(id);
        } catch (Exception ignored){}
        try {
            customerMapper.deleteById(id);
        } catch (Exception ignored){}

        return Result.ok();
    }

    @PostMapping("/modify_detail")
    public Result modifyDetail(String key, String value){
        init();
        try {
            UpdateWrapper<Customer> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", id).set(key, value);
            customerMapper.update(null, wrapper);
        } catch (Exception e){
            return Result.error().setMessage("Invalid key");
        }
        return Result.ok();
    }


}