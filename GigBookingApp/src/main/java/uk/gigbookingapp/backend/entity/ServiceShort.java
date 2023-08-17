package uk.gigbookingapp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;

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
    private String username;
    private String description;
    private Long pictureId;

    public ServiceShort(ServiceObj serviceObj){
        this.fee = serviceObj.getFee();
        this.id = serviceObj.getId();
        this.title = serviceObj.getTitle();
        this.providerId = serviceObj.getProviderId();
        this.username = serviceObj.getUsername();
        this.description = serviceObj.getDescription();
        this.pictureId = serviceObj.getPictureId();
    }

//    public static ServiceShort generateServiceShort(ServiceObj serviceObj){
//        ServiceShort serviceShort = new ServiceShort();
//        serviceShort.setFee(serviceObj.getFee());
//        serviceShort.setId(serviceObj.getId());
//        serviceShort.setTitle(serviceObj.getTitle());
//        serviceShort.setProviderId(serviceObj.getProviderId());
//        return serviceShort;
//    }

    public static LinkedList<ServiceShort> generateList(
            List<ServiceObj> list,
            ServiceProviderMapper providerMapper,
            ServicePicsMapper picsMapper){
        LinkedList<ServiceShort> linkedList = new LinkedList<>();
        for (ServiceObj serviceObj : list) {
            serviceObj.setUsername(providerMapper);
            serviceObj.setPictureId(picsMapper);
            linkedList.add(new ServiceShort(serviceObj));
        }
        return linkedList;
    }
}