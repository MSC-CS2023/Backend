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
    private Double balance = 500d;

    @JsonIgnore
    private String avatarPath;
    @JsonIgnore
    private Long avatarTimestamp;

    public void deposit(double money){
        this.balance += money;
    }

    public void withdraw(double money){
        if (this.balance > money){
            this.balance -= money;
        }
    }
}