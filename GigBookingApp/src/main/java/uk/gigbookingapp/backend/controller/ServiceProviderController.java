package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gigbookingapp.backend.entity.*;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/service_provider")
public class ServiceProviderController {
    @Autowired
    private ServiceProviderPasswordMapper passwordMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ServicePicsMapper servicePicsMapper;

    private CurrentId currentId;

    private ServicePics picture;

    private static final String picturePath = "/upload/picture/provider/";

    @Autowired
    ServiceProviderController(CurrentId currentId) {
        this.currentId = currentId;
    }

    @GetMapping("/services/get")
    public Result getServices(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        if (start < 0){
            return Result.error().setMessage("Invalid value of 'start'.");
        }
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        long id = currentId.getId();
        QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
        wrapper.eq("provider_id", id)
                .orderByDesc("timestamp")
                .last("limit " + start + ", " + num);
        List<ServiceShort> list = ServiceShort.generateList(serviceMapper.selectList(wrapper));

        return Result.ok().data("services", list);
    }

    @PutMapping("/service/add")
    public Result addService(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false, defaultValue = "") String detail,
            @RequestParam Double fee){
        long id = currentId.getId();
        ServiceObj serviceObj = new ServiceObj();

        serviceObj.setTitle(title);
        serviceObj.setDescription(description);
        serviceObj.setFee(fee);
        serviceObj.setDetail(detail);
        serviceObj.setProviderId(id);
        serviceMapper.insert(serviceObj);
        return Result.ok().data("service", serviceObj);
    }

    @PostMapping("/service/modify")
    public Result modifyService(
            @RequestParam("service_id") Long serviceId,
            @RequestParam String key,
            @RequestParam String value){
        long id = currentId.getId();

        QueryWrapper<ServiceObj> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", serviceId)
                .eq("provider_id", id);

        ServiceObj service = serviceMapper.selectOne(queryWrapper);
        if (service == null) { return Result.error().setMessage("Invalid Service id."); }

        if (key.compareToIgnoreCase("id") == 0 || key.compareToIgnoreCase("provider_id") == 0){
            return Result.error().setMessage("Invalid key.");
        }

        UpdateWrapper<ServiceObj> updateWrapper = new UpdateWrapper<>(service);
        try {
            updateWrapper.set(key, value);
            serviceMapper.update(null, updateWrapper);
        } catch (Exception e) {
            return Result.error().setMessage("Invalid key.");
        }

        return Result.ok().data("service", serviceMapper.selectById(serviceId));
    }

    @DeleteMapping("/service/delete")
    public Result deleteService(@RequestParam("service_id") Long serviceId){
        long id = currentId.getId();
        try {
            QueryWrapper<ServiceObj> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", serviceId)
                    .eq("provider_id", id);
            serviceMapper.delete(queryWrapper);
        } catch (Exception e) {
            return Result.error().setMessage("Cannot find the service id.");
        }
        return Result.ok();
    }

    @PostMapping("/service/add_pic")
    public Result updatePicture(
            @RequestParam("service_id") Long serviceId,
            MultipartFile pictureFile,
            HttpServletRequest request){
        long id = currentId.getId();
        this.picture = servicePicsMapper.selectById(id);
        deletePicture(request);
        String path = request.getServletContext().getRealPath(picturePath);
        String filename = id + "." +
                FilenameUtils.getExtension(pictureFile.getOriginalFilename());
        try {
            savaPicture(pictureFile, path, filename);
        } catch (Exception e) {
            return Result.error().setMessage("Add picture error");
        }

        UpdateWrapper<ServicePics> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",serviceId)
                .set("picture_path", picturePath + filename);
        servicePicsMapper.update(null, wrapper);
        return Result.ok();
    }

    public void savaPicture(MultipartFile picture, String path, String filename) throws Exception{
        File dir = new File(path);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                throw new IOException();
            }
        }
        String filePath = path + filename;
        File file = new File(filePath);
        picture.transferTo(file);
    }

    @PostMapping("/service/delete_pic")
    public void deletePicture(HttpServletRequest request){
        String path = request.getServletContext().getRealPath(picture.getPicPath());
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }

}
