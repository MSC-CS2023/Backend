package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.Favourite;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.ServiceShort;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.FavouriteMapper;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private FavouriteMapper favouriteMapper;
    @Autowired
    private ServiceMapper serviceMapper;

    private CurrentId currentId;


    @Autowired
    CustomerController(CurrentId currentId) {
        this.currentId = currentId;
    }

    @GetMapping("/favourite/get")
    public Result getFavourite(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        if (start < 0){
            return Result.error().setMessage("Invalid value of 'start'.");
        }
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        QueryWrapper<Favourite> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId())
                .orderByDesc("timestamp")
                .last("limit " + start + "," + num);
        List<Favourite> list = favouriteMapper.selectList(wrapper);
        for (Favourite f: list) {
            long id = f.getServiceId();
            f.setServiceShort(ServiceShort.generateServiceShort(serviceMapper.selectById(id)));
        }
        return Result.ok().data("booking_orders", list);
    }

    @GetMapping("/favourite/check")
    public Result checkFavourite(@RequestParam Long id){
        QueryWrapper<Favourite> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId())
                .eq("service_id", id);
        Favourite favourite = favouriteMapper.selectOne(wrapper);
        if (favourite == null){
            return Result.error();
        }
        return Result.ok();
    }

    @PutMapping("/favourite/add")
    private Result addFavourite(@RequestParam Long id){
        if(serviceMapper.selectById(id) == null){
            return Result.error().setMessage("Invalid service id.");
        }
        QueryWrapper<Favourite> wrapper = new QueryWrapper<Favourite>();
        wrapper.eq("service_id", id)
                .eq("customer_id", currentId.getId());
        if(favouriteMapper.selectOne(wrapper) != null){
            return Result.error().setMessage("The service is already in favourites.");
        }
        Favourite favourite = new Favourite();
        favourite.setCustomerId(currentId.getId());
        favourite.setServiceId(id);
        favourite.setServiceShort(ServiceShort.generateServiceShort(serviceMapper.selectById(id)));
        favouriteMapper.insert(favourite);
        return Result.ok().data("favourite", favourite);
    }

    @DeleteMapping("/favourite/delete")
    private Result deleteFavourite(@RequestParam Long id){
        if(serviceMapper.selectById(id) == null){
            return Result.error().setMessage("Invalid service id.");
        }
        QueryWrapper<Favourite> wrapper = new QueryWrapper<Favourite>();
        wrapper.eq("service_id", id)
                .eq("customer_id", currentId.getId());
        Favourite favourite = favouriteMapper.selectOne(wrapper);
        try {
            favouriteMapper.deleteById(favourite);
        } catch (Exception ignore){}
        return Result.ok();
    }


}
