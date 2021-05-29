package com.mmkpark.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.Msg;
import com.mmkpark.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class MsgController {

    @Autowired
    MsgService msgService;


    @GetMapping("msg/{name}")
    public Result msg(@PathVariable(name = "name") String name) {

        QueryWrapper<Msg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("m_garname",name);

        return Result.succ(msgService.getOne(queryWrapper));

    }

}
