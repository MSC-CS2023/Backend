package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.ServicePics;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.ServiceShort;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.Result;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping({"/public/customer", "/public/service_provider"})
public class PublicUserController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServicePicsMapper servicePicsMapper;
    @Autowired
    private ServiceMapper serviceMapper;


    private CurrentId currentId;
    private BaseMapper userMapper;
    private ServicePics picture;

    public PublicUserController(CurrentId currentId){
        this.currentId = currentId;
    }

    public void init(){
        if (currentId.getUsertype() == UserType.CUSTOMER){
            userMapper = customerMapper;
        } else {
            userMapper = providerMapper;
        }
    }

    @GetMapping("/avatar")
    public void getAvatar(
            @RequestParam Long id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        init();
        User user = (User) userMapper.selectById(id);
        MainController.sendAvatar(request, response, user, userMapper);
        return;
    }


    @GetMapping("/services")
    public Result services(
            @RequestParam Long id,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        if (currentId.getUsertype() != UserType.PROVIDER){
            return Result.error();
        }
        init();
        if (start < 0){
            return Result.error().setMessage("Invalid value of 'start'.");
        }
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
        wrapper.eq("provider_id", id)
                .orderByDesc("timestamp")
                .last("limit " + start + ", " + num);
        List<ServiceShort> list = ServiceShort.generateList(serviceMapper.selectList(wrapper), providerMapper, servicePicsMapper);

        return Result.ok().data("services", list);
    }

    @GetMapping("/get_detail")
    public Result getDetail(@RequestParam Long id){
        init();
        User user = (User) userMapper.selectById(id);
        if (user == null) {
            return Result.error().setMessage("User does not exist.");
        }
        return Result.ok().data("user", user);
    }
}
