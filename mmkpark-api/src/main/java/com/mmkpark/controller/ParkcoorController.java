package com.mmkpark.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.Garage;
import com.mmkpark.entity.Parkcoor;
import com.mmkpark.mapper.ParkcoorMapper;
import com.mmkpark.service.GarageService;
import com.mmkpark.service.ParkcoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/parkcoor")
public class ParkcoorController {

    @Autowired
    ParkcoorService parkcoorService;

    @Autowired
    ParkcoorMapper parkcoorMapper;

    @Autowired
    GarageService garageService;

    @GetMapping("/list/{name}")
    public Result list(@PathVariable(name = "name") String name) {

        QueryWrapper<Parkcoor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gar_name",name);
        List<Parkcoor> list = parkcoorService.list(queryWrapper);

        if(list!=null && !list.isEmpty()){

            Garage garage = new Garage();
            UpdateWrapper<Garage> updateWrapper = new UpdateWrapper<>();
            QueryWrapper<Parkcoor> queryWrapper2 = new QueryWrapper<>();
            updateWrapper.eq("garage_name",name);
            queryWrapper2.eq("status","false").eq("gar_name",name);

            garage.setParkRemain(list.size()-parkcoorMapper.selectCount(queryWrapper2));
            garage.setParkTotal(list.size());
            garageService.update(garage,updateWrapper);

            return Result.succ (list);

        }else {
            return Result.fail("此车库没有车位");
        }

    }

    @PostMapping("save")
    public Result save(@RequestBody List<Parkcoor> list) {

        parkcoorService.saveOrUpdateBatch(list);
        return Result.succ("更新成功");

    }

    @GetMapping("/emp/{name}")
    public Result delcoor(@PathVariable(name = "name") String name) {

        QueryWrapper<Parkcoor> delWrapper = new QueryWrapper<>();
        delWrapper.eq("gar_name",name);
        parkcoorService.remove(delWrapper);
        return Result.succ(200);
    }

}
