package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Springboot has had a class and an annotation called "Service".
@TableName("service")
public class ServiceObj {

    @TableId(type = IdType.NONE)
    private Long id;
    private String title;
    private String description;
//    private String detail;
    private Double fee;
    private Long providerId;
    private Long timestamp = System.currentTimeMillis();
    private String tag;
    @TableField(exist = false)
    private Long pictureId;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String address;

    public void setPictureId(ServicePicsMapper mapper){
        ServicePics servicePics = mapper.selectOne(new QueryWrapper<ServicePics>()
                .eq("service_id", this.id)
                .orderByDesc("timestamp")
                .last("limit 1"));

        this.pictureId = servicePics == null ? null : servicePics.getId();
    }

    public void setUsername(ServiceProviderMapper mapper){
        ServiceProvider serviceProvider = mapper.selectById(this.providerId);
        this.username = serviceProvider.getUsername();
        this.address = serviceProvider.getAddress();
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}