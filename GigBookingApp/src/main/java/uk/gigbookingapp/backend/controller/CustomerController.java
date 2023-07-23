package uk.gigbookingapp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.CustomerPasswordMapper;
import uk.gigbookingapp.backend.utils.Result;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerPasswordMapper passwordMapper;
    @Autowired
    private CustomerMapper customerMapper;

    private CurrentId currentId;
    @Autowired
    CustomerController(CurrentId currentId) {
        System.out.println(currentId);
        this.currentId = currentId;
    }

    @GetMapping("/self_details")
    public Result getSelfDetails(){
        int id = currentId.getId();
        Customer customer = customerMapper.selectById(id);
        System.out.println(currentId);
        return Result.ok().data("user", customer);
    }

    @DeleteMapping("/delete_account")
    public Result deleteAccount(){

        return Result.ok();
    }
}
