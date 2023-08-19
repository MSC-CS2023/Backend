package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.Result;

@RestController
@RequestMapping({"/customer/balance", "/service_provider/balance"})
public class BalanceController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;

    private CurrentId currentId;
    private BaseMapper userMapper;
    private User user;


    @Autowired
    BalanceController(CurrentId currentId) {
        this.currentId = currentId;
    }

    // Every request method in this class should begin with init()
    private void init(){
        long id = this.currentId.getId();
        if (currentId.getUsertype() == UserType.CUSTOMER){
            userMapper = customerMapper;
        } else {
            userMapper = providerMapper;
        }
        this.user = (User) userMapper.selectById(id);
    }

    @GetMapping("/get")
    public Result get(){
        init();
        return Result.ok().data("balance", user.getBalance());
    }

    @PostMapping("/deposit")
    public Result deposit(@RequestParam Double money){
        if (money < 0){
            return Result.error().setMessage("Invalid money number.");
        }
        user.deposit(money);
        userMapper.updateById(user);
        return Result.ok().data("balance", user.getBalance());
    }

    @PostMapping("/withdraw")
    public Result withdraw(@RequestParam Double money){
        if (money < 0){
            return Result.error().setMessage("Invalid money number.");
        }
        if (user.getBalance() < money){
            return Result.error().setMessage("Insufficient balance.");
        }
        user.withdraw(money);
        userMapper.updateById(user);
        return Result.ok().data("balance", user.getBalance());
    }
}
