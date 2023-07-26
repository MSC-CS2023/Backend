package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.ServiceProvider;
import uk.gigbookingapp.backend.entity.ServiceShort;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.ArrayList;
import java.util.LinkedList;
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

    private CurrentId currentId;

    @Autowired
    ServiceProviderController(CurrentId currentId) {
        this.currentId = currentId;
    }

    @GetMapping("/get_services")
    public Result getServices(Integer start, Integer num){
        int id = currentId.getId();

        QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
        wrapper.eq("provider_id", id);
        List<ServiceShort> list = ServiceShort.generateList(serviceMapper.selectList(wrapper));
        System.out.println(id);

        int len = list.size();

        if (start == null) {
            start = 0;
        } else if (start >= len){
            return Result.ok().
                    data("services", new ArrayList<>()).
                    setMessage("No new services.");
        }
        if (num == null || start + num >= len){
            list = list.subList(start, len);
        } else {
            list = list.subList(start, start + num);
        }

        return Result.ok().data("short_services", list);
    }

    @PutMapping("/add_service")
    public Result addService(String title, String description, String detail, Double fee){
        int id = currentId.getId();
        ServiceObj serviceObj = new ServiceObj();
        if (title == null) {
            return Result.error().setMessage("Title is null.");
        } else {
            serviceObj.setTitle(title);
        }
        if (description == null) {
            return Result.error().setMessage("Description is null.");
        } else {
            serviceObj.setDescription(description);
        }
        if (fee == null) {
            return Result.error().setMessage("Fee is null.");
        } else {
            serviceObj.setFee(fee);
        }
        serviceObj.setDetail(detail);
        serviceObj.setProviderId(id);
        serviceMapper.insert(serviceObj);
        return Result.ok().data("service", serviceObj);
    }

    @PostMapping("/service/modify")
    public Result modifyService(@RequestParam("service_id") Integer serviceId, String key, String value){
        int id = currentId.getId();
        if (serviceId == null) { return Result.error().setMessage("Service id is null."); }
        if (key == null) { return Result.error().setMessage("Key is null."); }
        if (value == null) { return Result.error().setMessage("Value is null."); }

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
    public Result deleteService(@RequestParam("service_id") Integer serviceId){
        if (serviceId == null) { return Result.error().setMessage("Service id is null."); }
        int id = currentId.getId();
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

}
