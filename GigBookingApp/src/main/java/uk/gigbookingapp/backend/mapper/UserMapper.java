package uk.gigbookingapp.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Service;
import uk.gigbookingapp.backend.entity.User;

@Service
public interface UserMapper extends BaseMapper<User> {}
