package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.Password;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

import java.io.File;
import java.io.IOException;

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
    private long id;
    private User user;
    private String avatarPath;

    private static final String customerAvatarPath = "/upload/avatar/customer/";
    private static final String providerAvatarPath = "/upload/avatar/provider/";


    @Autowired
    UserController(CurrentId currentId) {
        this.currentId = currentId;
    }

    // Every request method in this class should begin with init()
    private void init(){
        this.id = this.currentId.getId();
        if (currentId.getUsertype() == UserType.CUSTOMER){
            userMapper = customerMapper;
            passwordMapper = customerPasswordMapper;
            avatarPath = customerAvatarPath;
        } else {
            userMapper = providerMapper;
            passwordMapper = providerPasswordMapper;
            avatarPath = providerAvatarPath;
        }
        this.user = (User) userMapper.selectById(id);
    }

    @GetMapping("/self_details")
    public Result getSelfDetails(){
        init();
        User user = (User) userMapper.selectById(id);
        return Result.ok().data("user", user);
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
            userMapper.deleteById(id);
        } catch (Exception ignored){}

        return Result.ok();
    }

    @PostMapping("/modify_detail")
    public Result modifyDetail(
            @RequestParam String key,
            @RequestParam String value){
        init();
        try {
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", id).set(key, value);
            userMapper.update(null, wrapper);
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

    @PostMapping("/update_avatar")
    public Result updateAvatar(
            @RequestParam MultipartFile avatar,
            HttpServletRequest request){
        init();
        deleteUserAvatar(request);
        String path = request.getServletContext().getRealPath(avatarPath);
        String filename = id + "." +
                FilenameUtils.getExtension(avatar.getOriginalFilename());
        try {
            saveAvatar(avatar, path, filename);
        } catch (Exception e) {
            return Result.error().setMessage("Save file error.");
        }

        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("avatar_path", avatarPath + filename)
                .set("avatar_timestamp", System.currentTimeMillis());
        userMapper.update(null, wrapper);
        return Result.ok().data("timestamp", ((User) userMapper.selectById(id)).getAvatarTimestamp());
    }

    public void saveAvatar(MultipartFile avatar, String path, String filename) throws Exception{
        File dir = new File(path);
        if (!dir.exists()){
            if(!dir.mkdirs()){
                throw new IOException();
            }
        }
        String filePath = path + filename;
        File file = new File(filePath);
        avatar.transferTo(file);
    }

    public void deleteUserAvatar(HttpServletRequest request) {
        String path = request.getServletContext().getRealPath(user.getAvatarPath());
        if (path == null) {
            return;
        }
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }
    }

    @GetMapping("get_avatar")
    public void getAvatar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        init();
        MainController.sendAvatar(request, response, user, userMapper);
    }


    @GetMapping("/get_avatar_timestamp")
    public Result getAvatarTimestamp(){
        init();
        return Result.ok().data("timestamp", user.getAvatarTimestamp());
    }

    @GetMapping("/renew_token")
    public Result renewToken(){
        init();
        String token = JwtUtils.generateToken(user, currentId.getUsertype());
        Claims claims = JwtUtils.getClaimsByToken(token);
        Long exp = claims.getExpiration().getTime();
        return Result.ok().data("user", user).data("token", token).data("exp", exp);
    }


}