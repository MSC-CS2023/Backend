package uk.gigbookingapp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CustomerMapper customerMapper;

    @GetMapping("/find")
    public Result getUserList(){
        List<Customer> list = customerMapper.selectList(null);
        return Result.ok().data("userList", list);
    }

//    @PostMapping("/add")
//    public Result addUser(User user){
//        int i = userMapper.insert(user);
//        System.out.println(i);
//        return "1";
//    }
//
//    @GetMapping("/findId")
//    public Result getUserById(String id){
//        QueryWrapper<User> wrapper = new QueryWrapper<User>();
//        wrapper.eq("id", id);
//        System.out.println(userMapper.selectList(wrapper));
//        return "1";
//    }
//
//    @PutMapping("")
//    public Result updateUser(){
//        return "1";
//    }
//
//    @DeleteMapping("/{id}")
//    public Result deleteUserById(@PathVariable String id){
//        return "1";
//    }
//
//
//    // A json string is needed.
//    @PostMapping("/login")
//    public Result login(User user){
//
//        String token = JwtUtils.generateToken(user);
//        System.out.println(1111);
//        //return Result.ok();
//        return Result.ok().data("token", token);
//    }

//    @PostMapping("/upload")
//    public String uploadAvatar(String id, MultipartFile avatar, HttpServletRequest request) throws IOException{
//        System.out.println(id);
//        System.out.println(avatar.getContentType());
//        System.out.println(avatar.getOriginalFilename());
//        String path = request.getServletContext().getRealPath("upload");
//        System.out.println("path: " + path);
//        saveFile(avatar, path);
//        return "{'statue': OK}";
//    }
//
//    public void saveFile(MultipartFile avatar, String path) throws IOException{
//        File dir = new File(path);
//        if (!dir.exists()){
//            if (!dir.mkdir()) throw new IOException("Can not make");
//        }
//
//        File file = new File(path + avatar.getOriginalFilename());
//        avatar.transferTo(file);
//    }
}
