package com.mmkpark.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.CpwdDto;
import com.mmkpark.entity.User;
import com.mmkpark.mapper.UserMapper;
import com.mmkpark.service.UserService;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @GetMapping("list")
    public Result list() {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(User.class, info ->
                   !info.getColumn().equals("password"));
        List<User> list = userMapper.selectList(queryWrapper);

        return Result.succ (list);
    }

    @PostMapping("save")
    public Result save(@RequestBody User user) {

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username",user.getUsername());
        userService.update(user,updateWrapper);
        return Result.succ(200);

    }

    @PostMapping("cpwd")
    public Result cpwd(@RequestBody CpwdDto cpwdDto) {

        User user = userService.getOne(new QueryWrapper<User>().eq("username", cpwdDto.getUsername()));
        if(!user.getPassword().equals(SecureUtil.md5(cpwdDto.getOpwd()))){
            return Result.fail("原密码不正确");
        }
        user.setPassword(SecureUtil.md5(cpwdDto.getCpwd()));
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username",user.getUsername());
        userService.update(user,updateWrapper);
        return Result.succ(200);

    }

    @RequestMapping("register")
    public Result register(@RequestBody User user) {

        if(user.getPassword() == null)
            return Result.fail("没有密码");
        user.setPassword(SecureUtil.md5(user.getPassword()));
        userService.save(user);
        return Result.succ(200);

    }

    @GetMapping("/delusr/{id}")
    public Result delusr(@PathVariable(name = "id") Long id) {

        userService.removeById(id);
        return Result.succ(200);
    }



}
