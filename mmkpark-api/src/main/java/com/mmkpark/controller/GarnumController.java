package com.mmkpark.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.Garnum;
import com.mmkpark.entity.Parkcoor;
import com.mmkpark.mapper.GarageMapper;
import com.mmkpark.mapper.ParkcoorMapper;
import com.mmkpark.mapper.UserMapper;
import com.mmkpark.service.GarnumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumukang
 * @since 2021-04-27
 */
@RestController
public class GarnumController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    GarageMapper garageMapper;

    @Autowired
    ParkcoorMapper parkcoorMapper;

    @Autowired
    GarnumService garnumService;

    @GetMapping("total")
    public Result total() {

        Garnum garnum = new Garnum();
        garnum.setId(1);
        garnum.setAdminTotal(userMapper.selectCount(null));
        garnum.setGarTotal(garageMapper.selectCount(null));
        garnum.setCgarTotal(parkcoorMapper.selectCount(null));
        garnum.setCgarRenum(parkcoorMapper.selectCount(new QueryWrapper<Parkcoor>().eq("status","false")));
        garnumService.updateById(garnum);

        return Result.succ(garnumService.getById(1));

    }

}
