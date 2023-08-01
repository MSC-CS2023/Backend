package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    @TableId(type = IdType.NONE)
    private Long id;
    private String username;
    private String email;
    private String address;
    private String tel;

    @JsonIgnore
    private String avatarPath;
    private Long avatarTimestamp;
}