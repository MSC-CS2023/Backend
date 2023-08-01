package uk.gigbookingapp.backend.controller;

import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.BookingOrder;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.mapper.BookingOrderMapper;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/service_provider/booking_order")
public class ProviderOrderController {
    @Autowired
    BookingOrderMapper orderMapper;
    @Autowired
    CustomerController customerController;
    @Autowired
    ServiceProviderMapper providerMapper;
    @Autowired
    ServiceMapper serviceMapper;

    private CurrentId currentId;
    public ProviderOrderController(CurrentId currentId){
        this.currentId = currentId;
    }

    @GetMapping("/get")
    public Result get(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){

        MPJQueryWrapper<BookingOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(BookingOrder.class)
                .leftJoin("service s ON s.id = service_id")
                .eq("is_canceled", 0)
                .orderByDesc("creation_timestamp")
                .last("limit " + start + "," + num);

        List<BookingOrder> list = orderMapper.selectList(wrapper);
        return Result.ok().data("booking_orders", list);
    }

    @GetMapping("get_one")
    public Result getOne(@RequestParam Long id){
        MPJQueryWrapper<BookingOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(BookingOrder.class)
                .eq("id", id)
                .leftJoin("SERVICE s ON s.provider_id = " + currentId.getId());
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
        MPJQueryWrapper<BookingOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(BookingOrder.class)
                .leftJoin("SERVICE s ON s.provider_id = " + currentId.getId());
        if (or) {
            wrapper.isNull("t.id");
        } else {
            wrapper.isNotNull("t.id");
        }
        for (String val : keys){
            if (or) {
                wrapper.or();
            }
            wrapper.eq(val, not ? 0 : 1);
        }
        wrapper.groupBy("t.id")
                .orderByDesc("creation_timestamp")
                .last("limit " + start + "," + num);
        List<BookingOrder> list = orderMapper.selectList(wrapper);
        return Result.ok().data("booking_orders", list);
    }

    @PostMapping("/confirm")
    public Result confirm(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        order.setIsConfirmed(true);
        order.setConfirmationTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @PostMapping("/reject")
    public Result reject(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        order.setIsRejected(true);
        order.setRejectionTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

}