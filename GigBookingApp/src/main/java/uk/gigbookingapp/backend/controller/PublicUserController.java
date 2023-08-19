package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.Result;

import java.io.IOException;

@RestController
@RequestMapping({"/public/customer", "/public/service_provider"})
public class PublicUserController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;


    private CurrentId currentId;
    private BaseMapper userMapper;

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
