package uk.gigbookingapp.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favourite {
    /**
     * Remember set the "serviceShort" value when use this class.
     * */

    @TableId(type = IdType.NONE)
    private Long id;
    private Long serviceId;
    @TableField(exist = false)
    private ServiceShort serviceShort;
    private Long customerId;
    private Long timestamp = System.currentTimeMillis();
}
