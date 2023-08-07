package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.ServicePics;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.type.UserType;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping({"/public/customer", "/public/service_provider"})
public class PublicUserController {
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServicePicsMapper servicePicsMapper;


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
        sendAvatar(request, response, user, userMapper);
        return;
    }

    public static void sendAvatar(
            HttpServletRequest request,
            HttpServletResponse response,
            User user,
            BaseMapper userMapper) throws IOException {
        String userPath = user.getAvatarPath();
        String path = request.getServletContext().getRealPath(userPath);
        File file = new File(path);
        if (!file.exists()){
            user.setAvatarPath("");
            userMapper.updateById(user);
        }
        String filename = FilenameUtils.getName(userPath);
        System.out.println(filename);
        ServletOutputStream stream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType("application/octet-stream");
        stream.write(FileUtils.readFileToByteArray(file));
        stream.flush();
        stream.close();
    }


    @GetMapping("/get_pic")
    public void getPicture(
            @RequestParam Long id,
            HttpServletRequest request,
            HttpServletResponse response) throws  IOException {
        init();
        ServicePics picture = (ServicePics) userMapper.selectById(id);
        sendPicture(request, response, picture, servicePicsMapper);
        return;
    }

    public static void sendPicture(
            HttpServletRequest request,
            HttpServletResponse response,
            ServicePics picture,
            ServicePicsMapper pictureMapper) throws IOException{
        String picPath = picture.getPicPath();
        String path = request.getServletContext().getRealPath(picPath);
        File file = new File(path);
        if (!file.exists()){
            picture.setPicPath("");
            pictureMapper.updateById(picture);
        }
        String filename = FilenameUtils.getName(picPath);
        System.out.println(filename);
        ServletOutputStream stream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType("application/octet-stream");
        stream.write(FileUtils.readFileToByteArray(file));
        stream.flush();
        stream.close();
    }


}
