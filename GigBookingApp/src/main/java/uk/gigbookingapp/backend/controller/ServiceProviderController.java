package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gigbookingapp.backend.entity.CurrentId;
import uk.gigbookingapp.backend.entity.ServiceProvider;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderPasswordMapper;
import uk.gigbookingapp.backend.utils.Result;

@RestController
@RequestMapping("/service_provider")
public class ServiceProviderController {
    @Autowired
    private ServiceProviderPasswordMapper passwordMapper;
    @Autowired
    private ServiceProviderMapper providerMapper;
    @Autowired
    private ServiceMapper serviceMapper;

    private CurrentId currentId;

    @Autowired
    ServiceProviderController(CurrentId currentId) {
        this.currentId = currentId;
    }


}
