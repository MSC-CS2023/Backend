package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String detail;
    private Double fee;
    private Long providerId;
    private Long timestamp = System.currentTimeMillis();
    private String tag;

}