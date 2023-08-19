package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.ServicePics;
import uk.gigbookingapp.backend.entity.ServiceShort;
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

    private static final String picturePath = "/upload/picture/";

    @Autowired
    ServiceProviderController(CurrentId currentId) {
        this.currentId = currentId;
    }

    @GetMapping("/service/get")
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
        List<ServiceShort> list = ServiceShort.generateList(serviceMapper.selectList(wrapper), providerMapper, servicePicsMapper);

        return Result.ok().data("services", list);
    }

    @PutMapping("/service/add")
    public Result addService(
            @RequestParam String title,
            @RequestParam String description,
            //@RequestParam(required = false, defaultValue = "") String detail,
            @RequestParam Double fee,
            @RequestParam(required = false, defaultValue = "") String tag){
        long id = currentId.getId();
        ServiceObj serviceObj = new ServiceObj();

        serviceObj.setTitle(title);
        serviceObj.setDescription(description);
        serviceObj.setFee(fee);
        //serviceObj.setDetail(detail);
        serviceObj.setProviderId(id);
        serviceObj.setTag(tag);
        serviceMapper.insert(serviceObj);
        //serviceObj.setPictureId(servicePicsMapper);
        serviceObj.setUsername(providerMapper);
        return Result.ok().data("service", serviceObj);
    }

    @PostMapping("/service/modify")
    public Result modifyService(
            @RequestParam("service_id") Long serviceId,
            @RequestParam String key,
            @RequestParam String value){

        if (checkInvalidService(serviceId)) { return Result.error().setMessage("Invalid Service id."); }

        if (key.compareToIgnoreCase("id") == 0 || key.compareToIgnoreCase("provider_id") == 0){
            return Result.error().setMessage("Invalid key.");
        }
        ServiceObj service = serviceMapper.selectById(serviceId);
        UpdateWrapper<ServiceObj> updateWrapper = new UpdateWrapper<>(service);
        try {
            updateWrapper.set(key, value);
            serviceMapper.update(null, updateWrapper);
        } catch (Exception e) {
            return Result.error().setMessage("Invalid key.");
        }
        ServiceObj serviceObj = serviceMapper.selectById(serviceId);
        serviceObj.setPictureId(servicePicsMapper);
        serviceObj.setUsername(providerMapper);
        return Result.ok().data("service", serviceObj);
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

    @PutMapping ("/service/add_pic")
    public Result updatePicture(
            @RequestParam("service_id") Long serviceId,
            @RequestParam MultipartFile picture,
            HttpServletRequest request){
        if (checkInvalidService(serviceId)){
            return Result.error().setMessage("Invalid service id.");
        }
        String path = request.getServletContext().getRealPath(picturePath) + serviceId + "/";
        String filename = picture.getOriginalFilename();
        try {
            savePicture(picture, path, filename);
        } catch (Exception e) {
            return Result.error().setMessage("Add picture error");
        }
        ServicePics servicePics = new ServicePics();
        servicePics.setServiceId(serviceId);
        servicePics.setPicPath(picturePath + serviceId + "/" + filename);
        servicePicsMapper.insert(servicePics);
        return Result.ok();
    }

    public void savePicture(MultipartFile picture, String path, String filename) throws Exception{
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

    @DeleteMapping("/service/delete_pic")
    public Result deletePicture(
            @RequestParam Long id,
            HttpServletRequest request){
        ServicePics servicePic = servicePicsMapper.selectById(id);
        if (checkInvalidService(servicePic)){
            return Result.error().setMessage("Invalid service id.");
        }
        String path = request.getServletContext().getRealPath(servicePic.getPicPath());
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
        servicePicsMapper.deleteById(servicePic);
        return Result.ok();
    }


    private boolean checkInvalidService(Long serviceId) {
        QueryWrapper<ServiceObj> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", serviceId)
                .eq("provider_id", currentId.getId());

        ServiceObj service = serviceMapper.selectOne(queryWrapper);
        return service == null;
    }

    private boolean checkInvalidService(ServicePics servicePic) {
        if (servicePic == null){
            return true;
        } else {
            return checkInvalidService(servicePic.getServiceId());
        }
    }

}
