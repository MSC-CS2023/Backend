package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.BookingOrder;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.mapper.BookingOrderMapper;
import uk.gigbookingapp.backend.mapper.CustomerMapper;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/customer/booking_order")
public class CustomerOrderController {
    @Autowired
    BookingOrderMapper orderMapper;
    @Autowired
    CustomerController customerController;
    @Autowired
    ServiceProviderMapper providerMapper;
    @Autowired
    ServiceMapper serviceMapper;
    @Autowired
    CustomerMapper customerMapper;

    private CurrentId currentId;

    CustomerOrderController(CurrentId currentId){
        this.currentId = currentId;
    }

    @GetMapping("/get")
    public Result get(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        QueryWrapper<BookingOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId())
                .eq("is_canceled", 0)
                .orderByDesc("creation_timestamp")
                .last("limit " + start + "," + num);
        List<BookingOrder> list = orderMapper.selectList(wrapper);
        return Result.ok().data("booking_orders", list);
    }

    @GetMapping("get_one")
    public Result getOne(@RequestParam Long id){
        QueryWrapper<BookingOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId())
                .eq("id", id);
        BookingOrder order = orderMapper.selectOne(wrapper);
        if (order == null){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        return Result.ok().data("booking_orders", order);
    }

    @GetMapping("/advanced")
    public Result advanced(
            @RequestParam String[] keys,
            @RequestParam(required = false, defaultValue = "false") Boolean or,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num,
            @RequestParam(required = false, defaultValue = "false") Boolean not){
        if (start < 0){
            return Result.error().setMessage("Invalid value of 'start'.");
        }
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        QueryWrapper<BookingOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId());
        if (or) {
            wrapper.isNull("id");
        } else {
            wrapper.isNotNull("id");
        }
        for (String val : keys){
            if (or) {
                wrapper.or();
            }
            wrapper.eq(val, not ? 0 : 1);
        }
        wrapper.orderByDesc("creation_timestamp")
                .last("limit " + start + "," + num);
        List<BookingOrder> list = orderMapper.selectList(wrapper);
        return Result.ok().data("booking_orders", list);
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        order.setIsCanceled(true);
        order.setCancelTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @PostMapping("/finish")
    public Result finish(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        order.setIsFinished(true);
        order.setFinishTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @PostMapping("/mark")
    public Result mark(@RequestParam Long id, @RequestParam Integer mark){
        if (mark > 5 || mark < 1){
            return Result.error().setMessage("Invalid mark.");
        }
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        if (!order.getIsFinished()){
            return Result.error().setMessage("The order is not finished.");
        }
        order.setMark(mark);
        orderMapper.updateById(order);
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @PutMapping("/create")
    public Result create(
            @RequestParam("service_id") Long serviceId,
            @RequestParam("start_timestamp") Long startTimestamp,
            @RequestParam("end_timestamp") Long endTimestamp){
        if (startTimestamp < System.currentTimeMillis()){
            return Result.error().setMessage("The start time cannot be earlier than now.");
        }
        if (startTimestamp > endTimestamp){
            return Result.error().setMessage("The start time cannot be later than the end time.");
        }
        ServiceObj serviceObj = serviceMapper.selectById(serviceId);
        if (serviceObj == null){
            return Result.error().setMessage("Invalid id.");
        }
        BookingOrder order = new BookingOrder();
        order.setCustomerId(currentId.getId());
        //order.setCreationTimestamp(System.currentTimeMillis());
        order.setServiceId(serviceId);
        order.setStartTimestamp(startTimestamp);
        order.setEndTimestamp(endTimestamp);
        orderMapper.insert(order);
        return Result.ok().data("booking_order", order);
    }


}
