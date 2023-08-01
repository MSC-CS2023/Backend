package uk.gigbookingapp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceShort {

    private Long id;
    private String title;
    private Double fee;
    private Long providerId;

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