package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.*;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.type.TagsType;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.JwtUtils;
import uk.gigbookingapp.backend.utils.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private FavouriteMapper favouriteMapper;
    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private CustomerLogMapper customerLogMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;

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
            f.setServiceShort(new ServiceShort(serviceMapper.selectById(id), providerMapper));
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
    public Result addFavourite(@RequestParam Long id){
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
        favourite.setServiceShort(new ServiceShort(serviceMapper.selectById(id), providerMapper));
        favouriteMapper.insert(favourite);
        return Result.ok().data("favourite", favourite);
    }

    @DeleteMapping("/favourite/delete")
    public Result deleteFavourite(@RequestParam Long id){
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

    @GetMapping("/get_service")
    public Result getService(@RequestParam Long id){
        ServiceObj serviceObj = serviceMapper.selectById(id);
        if (serviceObj == null) {
            return Result.error().setMessage("Invalid id.");
        }
        CustomerLog customerLog = new CustomerLog();
        customerLog.setCustomerId(currentId.getId());
        customerLog.setServiceId(id);
        customerLogMapper.insert(customerLog);

        Customer customer = customerMapper.selectById(currentId.getId());
        calculateVector(customer, customerLog);
        return Result.ok().data("service", serviceObj);
    }

    private void calculateVector(Customer customer, CustomerLog log){
        List<Double> vector = customer.getVector();
        if (vector == null){
            vector = TagsType.initList(new ArrayList<>());
        } else {
            long timeDiff = log.getTimestamp() - customer.getPreferenceTimestamp();
            double t = (double) timeDiff / -86400000; // Turn the time to days.
            double k = Math.exp(t);
            for (int i = 0; i < vector.size(); i++) {
                vector.set(i, vector.get(i) * k);
            }
        }
        int index = TagsType.getIndex(serviceMapper.selectById(log.getServiceId()).getTag());
        vector.set(index, vector.get(index) + 1);
        customer.setVector(vector);
        customer.setPreferenceTimestamp(log.getTimestamp());
        customerMapper.updateById(customer);
    }

    @GetMapping("/get_rec")
    public Result getRec(@RequestParam(required = false, defaultValue = "10") Integer num){
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        long id = currentId.getId();
        List<Double> vector = customerMapper.selectById(id).getVector();
        if (vector == null) {
            return Result.ok().setMessage("New user.");
        }
        long nowTime = System.currentTimeMillis();
        int day = 10; // For fewer calculations, only data from active users within 10 days is required.
        long limitTime = nowTime - day * 86400000;
        List<Customer> customerList = customerMapper.selectList(
                new QueryWrapper<Customer>()
                        .ne("id", currentId.getId())
                        .isNotNull("preference_timestamp")
                        .gt("preference_timestamp", limitTime)
        );

        Long nearestId = getNearest(customerList, vector);
        if (nearestId == null){
            return Result.ok().setMessage("No nearest user.");
        }

        QueryWrapper<CustomerLog> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", nearestId)
                .notInSql("service_id",
                        "select service_id where customer_id = " + id)
                .gt("timestamp", limitTime)
                .orderByDesc("timestamp")
                .last("limit " + num);

        List<CustomerLog> logList = customerLogMapper.selectList(wrapper);

        System.out.println();

        List<ServiceShort> serviceShorts = new ArrayList<>();
        for (CustomerLog log : logList){
            serviceShorts.add(new ServiceShort(serviceMapper.selectById(log.getServiceId()), providerMapper));
        }

        return Result.ok().data("services", serviceShorts);

    }

    private Long getNearest(List<Customer> customerList, List<Double> vector) {
        double maxCosTheta = 0;
        Long nearestId = null;
        for (Customer customer : customerList){
            double cosTheta = cosineSimilarity(vector, customer.getVector());
            if (cosTheta > maxCosTheta){
                maxCosTheta = cosTheta;
                nearestId = customer.getId();
            }
        }
        return nearestId;
    }

    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        return vectorDot(vec1, vec2) / (vectorNorm(vec1) * vectorNorm(vec2));
    }

    private double vectorDot(List<Double> vec1, List<Double> vec2) {
        double sum = 0;
        for (int i = 0; i < vec1.size() && i < vec2.size(); i++)
            sum += vec1.get(i) * vec2.get(i);
        return sum;
    }

    private double vectorNorm(List<Double> vec) {
        double sum = 0;
        for (double v : vec)
            sum += v * v;
        return sum;
        // It should return Math.sqrt(sum), but that doesn't affect the result.
    }



}
