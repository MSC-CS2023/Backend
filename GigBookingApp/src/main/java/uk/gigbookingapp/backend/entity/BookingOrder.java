package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.type.OrderStateType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingOrder {
    /**
     * The time when the order is canceled.
     */
    private Long cancelTimestamp;
    /**
     * The time when the order is confirmed.
     */
    private Long confirmationTimestamp;
    /**
     * The time when the order is confirmed.
     */
    private Long creationTimestamp = System.currentTimeMillis();
    /**
     * The id of the order creater.
     */
    private Long customerId;
    /**
     * The time when the service end.
     */
    private Long endTimestamp;
    /**
     * The time when the service finished.
     */
    private Long finishTimestamp;
    /**
     * Order id.
     */
    @TableId(type = IdType.NONE)
    private Long id;
    /**
     * Is the order canceled by the creater.
     */
    private Boolean isCanceled = false;
    /**
     * Is the order confirmed by service provider.
     */
    private Boolean isConfirmed = false;
    /**
     * Is the service finished.
     */
    private Boolean isFinished = false;
    /**
     * Is the order rejected by service provider.
     */
    private Boolean isRejected = false;
    /**
     * The mark given by the customer for the order.
     */
    private Integer mark;
    /**
     * The time when the order is rejected.
     */
    private Long rejectionTimestamp;
    /**
     * The id of the service related to the order.
     */
    private Long serviceId;
    /**
     * The time when the service start.
     */
    private Long startTimestamp;

    @TableField(exist = false)
    private ServiceShort serviceShort;
    @TableField(exist = false)
    private String state;

    public void setServiceShort(
            ServiceMapper serviceMapper,
            ServicePicsMapper servicePicsMapper,
            ServiceProviderMapper providerMapper) {
        ServiceObj serviceObj = serviceMapper.selectById(this.serviceId);
        serviceObj.setUsername(providerMapper);
        serviceObj.setPictureId(servicePicsMapper);
        setServiceShort(new ServiceShort(serviceObj));
    }

    public void setServiceShort(ServiceObj serviceObj) {
        setServiceShort(new ServiceShort(serviceObj));
    }

    private void setServiceShort(ServiceShort serviceShort) {
        this.serviceShort = serviceShort;
    }

    public void setState(){
        if (isFinished){
            this.state = OrderStateType.FINISHED.toLowerCase();
        } else if (isCanceled){
            this.state = OrderStateType.CANCELED.toLowerCase();
        } else if (isRejected) {
            this.state = OrderStateType.REJECTED.toLowerCase();
        } else if (isConfirmed) {
            this.state = OrderStateType.PROCESSING.toLowerCase();
        } else {
            this.state = OrderStateType.UNCONFIRMED.toLowerCase();
        }
    }

}
