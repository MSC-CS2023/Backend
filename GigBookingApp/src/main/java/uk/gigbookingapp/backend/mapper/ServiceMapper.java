package uk.gigbookingapp.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import org.springframework.stereotype.Service;
import uk.gigbookingapp.backend.entity.ServiceObj;
@Service
public interface ServiceMapper extends MPJBaseMapper<ServiceObj> {}
