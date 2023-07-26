package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.*;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.Result;

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
    @Autowired
    private ServiceMapper serviceMapper;

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
        if (currentId.getUsertype() == UserType.PROVIDER){
            try {
                QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
                wrapper.eq("provider_id", id);
                serviceMapper.delete(wrapper);
            } catch (Exception ignored) {}
        }
        try {
            passwordMapper.deleteById(id);
        } catch (Exception ignored){}
        try {
            customerMapper.deleteById(id);
        } catch (Exception ignored){}

        return Result.ok();
    }

    @PostMapping("/modify_detail")
    public Result modifyDetail(
            @RequestParam String key,
            @RequestParam String value){
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

    @PostMapping("/reset_password")
    public Result resetPassword(
            @RequestParam("old_value") String oldValue,
            @RequestParam("new_value") String newValue){
        init();
        if (newValue.equals(oldValue)) return Result.error().setMessage("New value is same to the old one.");
        Password password = (Password) passwordMapper.selectById(id);
        if (!password.getPassword().equals(oldValue)){
            return Result.error().setMessage("Wrong old password!");
        }
        password.setPassword(newValue);
        passwordMapper.updateById(password);
        return Result.ok();
    }






}