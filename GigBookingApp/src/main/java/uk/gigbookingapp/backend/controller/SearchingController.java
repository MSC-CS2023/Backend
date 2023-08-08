package uk.gigbookingapp.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gigbookingapp.backend.entity.ServiceObj;
import uk.gigbookingapp.backend.entity.ServicePics;
import uk.gigbookingapp.backend.entity.ServiceShort;
import uk.gigbookingapp.backend.mapper.ServiceMapper;
import uk.gigbookingapp.backend.mapper.ServicePicsMapper;
import uk.gigbookingapp.backend.mapper.ServiceProviderMapper;
import uk.gigbookingapp.backend.type.SortType;
import uk.gigbookingapp.backend.utils.Result;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RestController
public class SearchingController {
    @Autowired
    ServiceMapper serviceMapper;
    @Autowired
    ServiceProviderMapper providerMapper;
    @Autowired
    ServicePicsMapper servicePicsMapper;

    @GetMapping("/search")
    public Result search(
            @RequestParam(value = "keywords") List<String> keywords,
            @RequestParam(value = "sort_by", required = false, defaultValue = "time") String sortBy,
            @RequestParam(required = false, defaultValue = "true") Boolean descending,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = "10") Integer num,
            @RequestParam(required = false, defaultValue = "false") Boolean or){

        if (!SortType.checkAvailable(sortBy)){
            return Result.error().setMessage("Invalid parameter 'sort_by");
        }
        if (start < 0){
            return Result.error().setMessage("Invalid value of 'start'.");
        }
        if (num < 0){
            return Result.error().setMessage("Invalid value of 'num'.");
        }
        String column = SortType.typeToColumn(sortBy);
        QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
        if (or) {
            wrapper.isNull("id");
        } else {
            wrapper.isNotNull("id");
        }
        for (String val : keywords){
            if (or) {
                wrapper.or();
            }
            wrapper.like("title", val);
        }
        if (descending){
            wrapper.orderByDesc(column);
        } else {
            wrapper.orderByAsc(column);
        }

        wrapper.last("limit " + start + "," + num);
        List<ServiceObj> list = serviceMapper.selectList(wrapper);
        LinkedList<ServiceShort> linkedList = ServiceShort.generateList(list, providerMapper);
        return Result.ok().data("services", linkedList);
    }

    @GetMapping("/get_random")
    public Result getRandom(@RequestParam Integer num){
        if (num < 0) {
            return Result.error().setMessage("Invalid num.");
        }
        QueryWrapper<ServiceObj> wrapper = new QueryWrapper<>();
        wrapper.last("order by rand() limit " + num);
        List<ServiceObj> list = serviceMapper.selectList(wrapper);
        LinkedList<ServiceShort> linkedList = ServiceShort.generateList(list, providerMapper);
        return Result.ok().data("services", linkedList);
    }

    @GetMapping("get_service")
    public Result getService(@RequestParam Long id){
        ServiceObj serviceObj = serviceMapper.selectById(id);
        if (serviceObj == null) {
            return Result.error().setMessage("Invalid id.");
        }
        return Result.ok().data("service", serviceObj);
    }


    @GetMapping("/get_pic")
    public void getPicture(
            @RequestParam Long id,
            HttpServletRequest request,
            HttpServletResponse response) throws  IOException {
        ServicePics picture = servicePicsMapper.selectById(id);
        String picPath = picture.getPicPath();
        String path = request.getServletContext().getRealPath(picPath);
        File file = new File(path);
        if (!file.exists()){
            servicePicsMapper.deleteById(picture);
            return;
        }
        MainController.sendPic(response, picPath, file);
    }

}
