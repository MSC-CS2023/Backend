package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.SessionObj;
import uk.gigbookingapp.backend.entity.User;
import uk.gigbookingapp.backend.mapper.*;
import uk.gigbookingapp.backend.type.UserType;
import uk.gigbookingapp.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping({"/customer/session", "/service_provider/session"})
public class SessionController {
    @Autowired
    private CustomerPasswordMapper customerPasswordMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServiceProviderPasswordMapper providerPasswordMapper;
    @Autowired
    private SessionMapper sessionMapper;

    private CurrentId currentId;
    private BaseMapper userMapper;
    private long id;
    private String type;
    private User user;


    @Autowired
    SessionController(CurrentId currentId) {
        this.currentId = currentId;
    }

    // Every request method in this class should begin with init()
    private void init(){
        this.id = this.currentId.getId();
        if (currentId.getUsertype() == UserType.CUSTOMER){
            userMapper = customerMapper;
            this.type = "customer";
        } else {
            userMapper = providerMapper;
            this.type = "service_provider";
        }
        this.user = (User) userMapper.selectById(id);
    }

    @PutMapping("/send")
    public Result send(
            @RequestParam("recipient_id") Long recipientId,
            @RequestParam("recipient_type") String recipientType,
            @RequestParam String message){
        init();
        SessionObj sessionObj = new SessionObj();
        sessionObj.setSender(currentId, userMapper);
        BaseMapper recipientMapper = switch (recipientType) {
            case "customer" -> customerMapper;
            case "service_provider", "service provider" -> providerMapper;
            default -> null;
        };
        try {
            CurrentId currentId1 = new CurrentId();
            currentId1.setId(recipientId);
            currentId1.setUsertype(recipientType);
            sessionObj.setRecipient(currentId1, recipientMapper);
        } catch (Exception e){
            return Result.error().setMessage("Invalid recipient information.");
        }
        sessionObj.setMessage(message);
        sessionMapper.insert(sessionObj);
        return Result.ok().data("message", sessionObj);
    }

    @GetMapping("get_one_history")
    public Result getOneHistory(
            @RequestParam Long id,
            @RequestParam String type,
            @RequestParam Long from,
            @RequestParam(required = false) Long until,
            @RequestParam(value = "only_received", required = false, defaultValue = "false") Boolean only,
            @RequestParam(required = false) Integer num){
        init();
        BaseMapper recipientMapper = switch (type) {
            case "customer" -> customerMapper;
            case "service_provider", "service provider" -> providerMapper;
            default -> null;
        };
        User recipient = (User) recipientMapper.selectById(id);
        if (recipient == null){
            return Result.error().setMessage("Invalid recipient information.");
        }
        QueryWrapper<SessionObj> wrapper = new QueryWrapper<>();
        wrapper.nested(i -> i
                .eq("sender_id", id)
                .eq("sender_type", type)
                .eq("recipient_id", this.id)
                .eq("recipient_type", this.type));
        if (!only){
            wrapper.or(i -> i
                    .eq("sender_id", this.id)
                    .eq("sender_type", this.type)
                    .eq("recipient_id", id)
                    .eq("recipient_type", type));
        }
        wrapper.ge("timestamp", from);
        if (until != null){
            wrapper.lt("timestamp", until);
        }
        wrapper.orderByDesc("timestamp");
        if (num != null){
            wrapper.last("limit " + num);
        }

        List<SessionObj> sessionObjs = sessionMapper.selectList(wrapper);
        for (SessionObj sessionObj : sessionObjs){
            sessionObj.setUnread(false);
            sessionMapper.updateById(sessionObj);
        }
        return Result.ok().data("sessionObjs", sessionObjs);
    }

    @GetMapping("get_one_unread")
    public Result getOneUnread(
            @RequestParam Long id,
            @RequestParam String type){
        init();
        BaseMapper recipientMapper = switch (type) {
            case "customer" -> customerMapper;
            case "service_provider", "service provider" -> providerMapper;
            default -> null;
        };
        User recipient = (User) recipientMapper.selectById(id);
        if (recipient == null){
            return Result.error().setMessage("Invalid recipient information.");
        }
        QueryWrapper<SessionObj> wrapper = new QueryWrapper<>();
        wrapper.eq("sender_id", id)
                .eq("sender_type", type)
                .eq("recipient_id", this.id)
                .eq("recipient_type", this.type)
                .eq("unread", true)
                .orderByDesc("timestamp");

        List<SessionObj> sessionObjs = sessionMapper.selectList(wrapper);
        for (SessionObj sessionObj : sessionObjs){
            sessionObj.setUnread(false);
            sessionMapper.updateById(sessionObj);
        }
        return Result.ok().data("sessionObjs", sessionObjs);
    }

//    @GetMapping("get_chatted")
//    public Result getChattedPeople(@RequestParam(required = false, defaultValue = "10") Integer num){
//        init();
//        QueryWrapper<SessionObj> wrapper = new QueryWrapper<>();
//        wrapper.nested(i -> i
//                        .eq("sender_id", this.id)
//                        .eq("sender_type", this.type))
//                .or(i -> i
//                        .eq("recipient_id", this.id)
//                        .eq("recipient_type", this.type));
//        sessionMapper.selectList(wrapper);
//
//    }

}
