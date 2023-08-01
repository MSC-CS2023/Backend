package uk.gigbookingapp.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {
//    @Autowired
//    private CustomerPasswordMapper passwordMapper;
//    @Autowired
//    private CustomerMapper customerMapper;
//
//    private CurrentId currentId;
//
//    @Autowired
//    CustomerController(CurrentId currentId) {
//        this.currentId = currentId;
//    }
//
//    @GetMapping("/self_details")
//    public Result getSelfDetails(){
//        int id = this.currentId.getId();
//        Customer customer = customerMapper.selectById(id);
//        return Result.ok().data("user", customer);
//    }
//
//    @DeleteMapping("/delete_account")
//    public Result deleteAccount(){;
//        int id = this.currentId.getId();
//        try {
//            passwordMapper.deleteById(id);
//        } catch (Exception ignored){}
//        try {
//            customerMapper.deleteById(id);
//        } catch (Exception ignored){}
//
//        return Result.ok();
//    }
//
//    @PostMapping("/modify_detail")
//    public Result modifyDetail(String key, String value){
//        int id = this.currentId.getId();
//        try {
//            UpdateWrapper<Customer> wrapper = new UpdateWrapper<>();
//            wrapper.eq("id", id).set(key, value);
//            customerMapper.update(null, wrapper);
//        } catch (Exception e){
//            return Result.error().setMessage("Invalid key");
//        }
//        return Result.ok();
//    }
}
