package uk.gigbookingapp.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceShort {
    @JsonProperty("uid")
    private Long id;
    private String title;
    private Double fee;
    @JsonProperty("provider_id")
    private Long providerId;

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

    public static ServiceShort generateServiceShort(ServiceObj serviceObj){
        ServiceShort serviceShort = new ServiceShort();
        serviceShort.setFee(serviceObj.getFee());
        serviceShort.setId(serviceObj.getId());
        serviceShort.setTitle(serviceObj.getTitle());
        serviceShort.setProviderId(serviceObj.getProviderId());
        return serviceShort;
    }

    public static LinkedList<ServiceShort> generateList(List<ServiceObj> list){
        LinkedList<ServiceShort> linkedList = new LinkedList<>();
        for (ServiceObj serviceObj : list) {
            linkedList.add(generateServiceShort(serviceObj));
        }
        return linkedList;
    }
}
