package uk.gigbookingapp.backend.controller;

import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.BookingOrder;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.type.OrderStateType;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping({"/service_provider/booking_order", "/customer/booking_order"})
public class GetOrderController {
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
    public GetOrderController(CurrentId currentId){
        this.currentId = currentId;
    }

    @GetMapping("/get_unconfirmed")
    public Result getUnconfirmed(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        return getState(start, num, OrderStateType.UNCONFIRMED);
    }

    @GetMapping("/get_processing")
    public Result getProcessing(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        return getState(start, num, OrderStateType.PROCESSING);
    }
    @GetMapping("/get_rejected")
    public Result getRejected(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        return getState(start, num, OrderStateType.REJECTED);
    }
    @GetMapping("/get_canceled")
    public Result getCanceled(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        return getState(start, num, OrderStateType.CANCELED);
    }
    @GetMapping("/get_finished")
    public Result getFinished(
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num){
        return getState(start, num, OrderStateType.FINISHED);
    }

    private Result getState(Integer start, Integer num, String state){
        MPJQueryWrapper<BookingOrder> wrapper = new MPJQueryWrapper<>();
        if (currentId.getUsertype() == UserType.CUSTOMER){
            wrapper.selectAll(BookingOrder.class)
                    .eq("customer_id", currentId.getId());
        } else {
            wrapper.selectAll(BookingOrder.class)
                    .leftJoin("service s ON s.id = service_id")
                    .eq("s.provider_id", currentId.getId());
        }
        queryByState(wrapper, state);
        wrapper.orderByDesc("creation_timestamp")
                .last("limit " + start + "," + num);

        List<BookingOrder> list = orderMapper.selectList(wrapper);
        list.forEach(bookingOrder -> {
            bookingOrder.setServiceShort(serviceMapper, servicePicsMapper, providerMapper);
            bookingOrder.setStateAndAddress(customerMapper);
        });
        return Result.ok().data("booking_orders", list);
    }

    private void queryByState(MPJQueryWrapper<BookingOrder> wrapper, String state){
        // is_confirmed, is_rejected, is_cancelled, is_finished
        switch (state){
            case OrderStateType.UNCONFIRMED ->
                    wrapper.eq("is_confirmed", false)
                            .eq("is_rejected", false)
                            .eq("is_canceled", false)
                            .eq("is_finished", false);
            case OrderStateType.PROCESSING ->
                wrapper.eq("is_confirmed", true)
                        .eq("is_rejected", false)
                        .eq("is_canceled", false)
                        .eq("is_finished", false);
            case OrderStateType.REJECTED ->
                    wrapper.eq("is_rejected", true)
                            .eq("is_canceled", false)
                            .eq("is_finished", false);
            case OrderStateType.CANCELED ->
                    wrapper.eq("is_canceled", true)
                            .eq("is_finished", false);
            case OrderStateType.FINISHED ->
                    wrapper.eq("is_finished", true);
        }

    }
}
