package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

//Springboot has had a class and an annotation called "Service".
@TableName("service")
public class ServiceObj {

    @TableId(type = IdType.NONE)
    private Long id;
    private String title;
    private String description;
    private String detail;
    private Double fee;
    @JsonProperty("provider_id")
    private Long providerId;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @JsonIgnore
    private Timestamp timestamp;

    @JsonProperty("timestamp")
    @TableField(exist = false)
    private Long timestampLong;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        setTimestampLong();
    }

    public Long getTimestampLong() {
        return this.timestampLong;
    }

    public void setTimestampLong() {
        this.timestampLong = timestamp.getTime();
    }
}
