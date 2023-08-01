package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
