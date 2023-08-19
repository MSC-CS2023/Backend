package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.ServiceProvider;
import uk.gigbookingapp.backend.entity.ServiceShort;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping( "/public/service_provider")
public class PublicProviderController {
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServicePicsMapper servicePicsMapper;
    @Autowired
    private ServiceMapper serviceMapper;

    @GetMapping("/services")
    public Result services(
            @RequestParam Long id,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        if (start < 0){
            return Result.error().setMessage("Invalid value of 'start'.");
        }
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        if (providerMapper.selectById(id) == null){
            return Result.error().setMessage("The user does not exist.");
        }
        QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
        wrapper.eq("provider_id", id)
                .orderByDesc("timestamp")
                .last("limit " + start + ", " + num);
        List<ServiceShort> list = ServiceShort.generateList(serviceMapper.selectList(wrapper), providerMapper, servicePicsMapper);

        return Result.ok().data("services", list);
    }

    @GetMapping("/get_mark")
    public Result getMark(@RequestParam Long id){
        ServiceProvider provider = providerMapper.selectById(id);
        if (provider == null){
            return Result.error().setMessage("The user does not exist.");
        }
        return Result.ok().data("balance", provider.getMark());
    }

}
