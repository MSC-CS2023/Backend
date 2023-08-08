package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.User;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@MapperScan("uk.gigbookingapp.backend.mapper")
public class MainController {

    /**
     * There are some tool functions. They are required by multiple different classes.
     * */

    public static void sendPic(HttpServletResponse response, String picPath, File file) throws IOException {
        String filename = FilenameUtils.getName(picPath);
        System.out.println(filename);
        ServletOutputStream stream = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType("application/octet-stream");
        stream.write(FileUtils.readFileToByteArray(file));
        stream.flush();
        stream.close();
    }

    public static void sendAvatar(
            HttpServletRequest request,
            HttpServletResponse response,
            User user,
            BaseMapper userMapper) throws IOException {
        String userPath = user.getAvatarPath();
        if (userPath == null || userPath.trim().isEmpty()){
            return;
        }
        String path = request.getServletContext().getRealPath(userPath);
        File file = new File(path);
        if (!file.exists()){
            user.setAvatarPath(null);
            user.setAvatarTimestamp(null);
            userMapper.updateById(user);
            return;
        }
        sendPic(response, userPath, file);
    }

    @GetMapping("hello")
    public String hello(){
        return "hello world123";
    }

//    @PostMapping("/find_password")
//    public Result findPassword(String email){
//
//    }
}
