package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gigbookingapp.backend.type.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("session")
public class SessionObj {
    @TableId(type = IdType.NONE)
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String senderType;
    private String recipientType;
    private String message;
    private Long timestamp = System.currentTimeMillis();
    @JsonIgnore
    private Boolean unread = true;

//    @TableField(exist = false)
//    private String senderUsername;
//    @TableField(exist = false)
//    private String recipientUsername;

    public Integer getSenderTypeInt(){
        return switch (senderType) {
            case "customer" -> UserType.CUSTOMER;
            case "service_provider", "service provider" -> UserType.PROVIDER;
            default -> null;
        };
    }

    public Integer getRecipientTypeInt(){
        return switch (recipientType) {
            case "customer" -> UserType.CUSTOMER;
            case "service_provider", "service provider" -> UserType.PROVIDER;
            default -> null;
        };
    }

    public void setSenderType(int type){
        if (type == UserType.CUSTOMER){
            this.senderType = "customer";
        } else if (type == UserType.PROVIDER) {
            this.senderType = "service_provider";
        }
    }

    public void setRecipientType(int type){
        if (type == UserType.CUSTOMER){
            this.recipientType = "customer";
        } else if (type == UserType.PROVIDER) {
            this.recipientType = "service_provider";
        }
    }

    public void setSender(Long id, int type, String username){
        setSenderId(id);
        setSenderType(type);
//        setSenderUsername(username);
    }

    public void setSender(CurrentId currentId, BaseMapper<User> userMapper){
        long id = currentId.getId();
        String username = userMapper.selectById(id).getUsername();
        setSender(id, currentId.getUsertype(), username);
    }

    public void setRecipient(Long id, int type, String username){
        setRecipientId(id);
        setRecipientType(type);
//        setRecipientUsername(username);
    }

    public void setRecipient(CurrentId currentId, BaseMapper<User> userMapper) {
        long id = currentId.getId();
        String username = userMapper.selectById(id).getUsername();
        setRecipient(id, currentId.getUsertype(), username);
    }

}
