package uk.gigbookingapp.backend.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
        import org.apache.ibatis.reflection.MetaObject;
        import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;


@Component
public class TimeMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {

        this.setFieldValByName("timestamp", new Timestamp(System.currentTimeMillis()), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("timestamp", new Timestamp(System.currentTimeMillis()), metaObject);
    }
}


