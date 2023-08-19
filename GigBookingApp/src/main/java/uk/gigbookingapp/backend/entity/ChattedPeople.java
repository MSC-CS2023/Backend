package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import uk.gigbookingapp.backend.mapper.SessionMapper;
import uk.gigbookingapp.backend.type.UserType;

public class ChattedPeople {
    private Long id;
    private String type;
    private String username;
    private Long timestamp;
    private Integer unreadNum;

    public void setType(String type) {
        this.type = type;
    }

    public void setType(int type){
        if (type == UserType.CUSTOMER){
            this.type = "customer";
        } else if (type == UserType.PROVIDER) {
            this.type = "service_provider";
        }
    }

    public Integer getTypeInt(){
        return switch (type) {
            case "customer" -> UserType.CUSTOMER;
            case "service_provider", "service provider" -> UserType.PROVIDER;
            default -> null;
        };
    }

    public ChattedPeople(CurrentId currentId, BaseMapper userMapper, SessionMapper sessionMapper){
        this.id = currentId.getId();
        setType(currentId.getUsertype());
        this.username = ((User) userMapper.selectById(id)).getUsername();
    }

}
