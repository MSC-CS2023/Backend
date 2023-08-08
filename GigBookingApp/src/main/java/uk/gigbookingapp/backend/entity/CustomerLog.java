package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLog {
    @TableId(type = IdType.NONE)
    private Long id;
    private Long customerId;
    private Long serviceId;
    private Long timestamp = System.currentTimeMillis();
}
