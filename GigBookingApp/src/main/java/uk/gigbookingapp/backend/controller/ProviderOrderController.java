package uk.gigbookingapp.backend.controller;

import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.BookingOrder;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.Customer;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/service_provider/booking_order")
public class ProviderOrderController {
    @Autowired
    BookingOrderMapper orderMapper;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    ServiceProviderMapper providerMapper;
    @Autowired
    ServiceMapper serviceMapper;
    @Autowired
    ServicePicsMapper servicePicsMapper;

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
                .eq("s.provider_id", currentId.getId())
                .orderByDesc("creation_timestamp")
                .last("limit " + start + "," + num);

        List<BookingOrder> list = orderMapper.selectList(wrapper);
        list.forEach(bookingOrder -> {
            bookingOrder.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
            bookingOrder.setState();
        });
        return Result.ok().data("booking_orders", list);
    }

    @GetMapping("get_one")
    public Result getOne(@RequestParam Long id){
        MPJQueryWrapper<BookingOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(BookingOrder.class)
                .eq("t.id", id)
                .leftJoin("SERVICE s ON s.id = " + currentId.getId());
        BookingOrder order = orderMapper.selectOne(wrapper);
        if (order == null){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        order.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
        order.setState();
        return Result.ok().data("booking_order", order);
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
                .leftJoin("service s ON s.id = service_id")
                .eq("s.provider_id", currentId.getId());
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
        list.forEach(bookingOrder -> {
            bookingOrder.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
            bookingOrder.setState();
        });
        return Result.ok().data("booking_orders", list);
    }

    @PostMapping("/confirm")
    public Result confirm(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null ||
                !Objects.equals(serviceMapper.selectById(order.getServiceId()).getProviderId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        if (order.getIsRejected()){
            return Result.error().setMessage("The order has been rejected.");
        }
        if (order.getIsCanceled()){
            return Result.error().setMessage("The order has been canceled.");
        }
        if (order.getIsFinished()){
            return Result.error().setMessage("The order has been finished.");
        }
        order.setIsConfirmed(true);
        order.setConfirmationTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        order.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
        order.setState();
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @PostMapping("/reject")
    public Result reject(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null ||
                !Objects.equals(serviceMapper.selectById(order.getServiceId()).getProviderId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        if (order.getIsRejected()){
            return Result.error().setMessage("The order has been rejected already.");
        }
        ServiceObj serviceObj = serviceMapper.selectById(order.getServiceId());
        if (order.getIsCanceled()){
            return Result.error().setMessage("The order has been canceled.");
        } else {
            Customer customer = customerMapper.selectById(order.getCustomerId());
            customer.deposit(serviceObj.getFee());
        }
        if (order.getIsFinished()){
            return Result.error().setMessage("The order has been finished.");
        }

        order.setIsRejected(true);
        order.setRejectionTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        order.setServiceShort(serviceObj, providerMapper);
        order.setState();
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @GetMapping("/get_by_date")
    public Result getByDate(
            @RequestParam Long timestamp){
        long start = timestamp;
        long end = timestamp + 86400 * 1000;
        MPJQueryWrapper<BookingOrder> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(BookingOrder.class)
                .leftJoin("service s ON s.id = service_id")
                .eq("s.provider_id", currentId.getId())
                .ge("start_timestamp", start)
                .lt("start_timestamp", end)
                .orderByDesc("creation_timestamp");

        List<BookingOrder> list = orderMapper.selectList(wrapper);
        list.forEach(bookingOrder -> {
            bookingOrder.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
            bookingOrder.setState();
        });
        return Result.ok().data("booking_orders", list);
    }

}
