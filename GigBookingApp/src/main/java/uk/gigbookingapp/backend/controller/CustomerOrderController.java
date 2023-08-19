package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.*;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.utils.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/customer/booking_order")
public class CustomerOrderController {
    @Autowired
    BookingOrderMapper orderMapper;
    @Autowired
    ServiceProviderMapper providerMapper;
    @Autowired
    ServiceMapper serviceMapper;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    ServicePicsMapper servicePicsMapper;

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
        QueryWrapper<BookingOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId())
                .eq("id", id);
        BookingOrder order = orderMapper.selectOne(wrapper);
        if (order == null){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        order.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
        order.setState();
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
        list.forEach(bookingOrder -> {
            bookingOrder.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
            bookingOrder.setState();
        });
        return Result.ok().data("booking_orders", list);
    }

    @PostMapping("/cancel")
    public Result cancel(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        if (order.getIsCanceled()){
            return Result.error().setMessage("The order has been canceled already.");
        }
        if (order.getIsFinished()){
            return Result.error().setMessage("The order has been finished.");
        }
        ServiceObj serviceObj = serviceMapper.selectById(order.getServiceId());
        if (!order.getIsRejected()){
            Customer customer = customerMapper.selectById(currentId.getId());
            customer.withdraw(serviceObj.getFee());
        }
        order.setIsCanceled(true);
        order.setCancelTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        order.setServiceShort(serviceObj, providerMapper);
        order.setState();
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    @PostMapping("/finish")
    public Result finish(@RequestParam Long id){
        BookingOrder order = orderMapper.selectById(id);
        if (order == null || !Objects.equals(order.getCustomerId(), currentId.getId())){
            return Result.error().setMessage("The order with the given id does not belong to the user.");
        }
        if (!order.getIsConfirmed()){
            return Result.error().setMessage("The order has not been confirmed.");
        }
        if (order.getIsRejected()){
            return Result.error().setMessage("The order has been rejected.");
        }
        if (order.getIsCanceled()){
            return Result.error().setMessage("The order has been canceled.");
        }
        if (order.getIsFinished()){
            return Result.error().setMessage("The order has been finished already.");
        }

        ServiceObj serviceObj = serviceMapper.selectById(order.getServiceId());
        ServiceProvider provider = providerMapper.selectById(serviceObj.getProviderId());
        provider.deposit(serviceObj.getFee());
        providerMapper.updateById(provider);
        order.setIsFinished(true);
        order.setFinishTimestamp(System.currentTimeMillis());
        orderMapper.updateById(order);
        order.setServiceShort(serviceObj, providerMapper);
        order.setState();
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
        calculateMark(serviceMapper.selectById(order.getServiceId()).getProviderId());
        order.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
        order.setState();
        return Result.ok().data("booking_order", orderMapper.selectById(id));
    }

    private void calculateMark(long id){
        ServiceProvider provider = providerMapper.selectById(id);
        MPJLambdaWrapper<BookingOrder> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAvg(BookingOrder::getMark)
                .leftJoin(" service s on s.id = t.service_id")
                .eq("s.provider_id", id);
        Double d = orderMapper.selectJoinOne(Double.class, wrapper);
        System.out.println(d);
        d = d == null ? 0 : d;
        provider.setMark(d);
        providerMapper.updateById(provider);
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

        // Pre-pay the money
        Customer customer = customerMapper.selectById(currentId.getId());
        if (serviceObj.getFee() > customer.getBalance()){
            return Result.error().setMessage("Insufficient balance.");
        } else {
            customer.withdraw(serviceObj.getFee());
        }

        BookingOrder order = new BookingOrder();
        order.setCustomerId(currentId.getId());
        order.setServiceId(serviceId);
        order.setStartTimestamp(startTimestamp);
        order.setEndTimestamp(endTimestamp);
        orderMapper.insert(order);
        order.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
        order.setState();
        return Result.ok().data("booking_order", order);
    }

    @GetMapping("/get_by_date")
    public Result getByDate(
            @RequestParam Integer day,
            @RequestParam Integer month,
            @RequestParam Integer year) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date startDate = sdf.parse(String.format("%4d%2d%2d", year, month, day));
        Date endDate = sdf.parse(String.format("%4d%2d%2d", year, month, day + 1));
        long start = startDate.getTime();
        long end = endDate.getTime();
        QueryWrapper<BookingOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", currentId.getId())
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
