package com.mmkpark.controller;


import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.Device;
import com.mmkpark.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * <p>
 * 代码生成业务表字段 前端控制器
 * </p>
 *
 * @author mumukang
 * @since 2021-04-22
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @GetMapping("list")
    public Result list() {

        List<Device> list = deviceService.list();
        return Result.succ (list);

    }

}
