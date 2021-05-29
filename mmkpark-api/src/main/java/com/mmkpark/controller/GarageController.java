package com.mmkpark.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.Garage;
import com.mmkpark.entity.Msg;
import com.mmkpark.entity.Parkcoor;
import com.mmkpark.mapper.MsgMapper;
import com.mmkpark.mqtt.IMqttSender;
import com.mmkpark.service.GarageService;
import com.mmkpark.service.MsgService;
import com.mmkpark.service.ParkcoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumukang
 * @since 2021-04-22
 */
@RestController
@RequestMapping("/garage")
public class GarageController {

    @Autowired
    GarageService garageService;

    @Autowired
    ParkcoorService parkcoorService;

    @Autowired
    MsgService msgService;

    @Autowired
    MsgMapper msgMapper;

    @Autowired
    IMqttSender iMqttSender;


    @GetMapping("list")
    public Result list() {

        List<Garage> list = garageService.list();
        return Result.succ (list);

    }

    @GetMapping("{name}")
    public Result getone(@PathVariable(name = "name") String name) {

        QueryWrapper<Garage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("garage_name",name);

        return Result.succ(garageService.getOne(queryWrapper));

    }

    @PostMapping("save")
    public Result save(@RequestBody Garage garage) {

        UpdateWrapper<Garage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("garage_name",garage.getGarageName());
        garageService.saveOrUpdate(garage,updateWrapper);

        if(msgMapper.selectCount(new QueryWrapper<Msg>().eq("m_garname",garage.getGarageName()))==0){
            Msg msg = new Msg();
            msg.setMGarname(garage.getGarageName());
            msgService.save(msg);
        }
        return Result.succ("更新成功");

    }


    @GetMapping("/delgar/{id}")
    public Result delgar(@PathVariable(name = "id") Long id) {

        Garage garage = garageService.getById(id);
        QueryWrapper<Parkcoor> delWrapper = new QueryWrapper<>();
        delWrapper.eq("gar_name",garage.getGarageName());

        QueryWrapper<Msg> delWrapper2 = new QueryWrapper<>();
        delWrapper2.eq("m_garname",garage.getGarageName());

        parkcoorService.remove(delWrapper);
        garageService.removeById(id);
        msgService.remove(delWrapper2);

        return Result.succ(200);
    }


    @PostMapping("detect")
    public Result detect(@RequestBody Garage garage) {

        Garage gar = garageService.getOne(new QueryWrapper<Garage>().eq("garage_name", garage.getGarageName()));
        HashMap hashMap = new HashMap<String,String>();
        hashMap.put("mGarname",gar.getGarageName());
        hashMap.put("videourl",gar.getGarageVideo());
        hashMap.put("pic",gar.getGaragePic());
        JSONObject jsonObj = new JSONObject(hashMap);
        iMqttSender.sendToMqtt("pysub",jsonObj.toString());
        return Result.succ("调用成功");

    }


}
