package com.mmkpark.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmkpark.common.lang.Result;
import com.mmkpark.entity.LoginDto;
import com.mmkpark.entity.User;
import com.mmkpark.service.UserService;
import com.mmkpark.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @RequestMapping("/login")
    public Result login(@RequestBody LoginDto loginDto, HttpServletResponse response) {

        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));

        if(user == null){
            return Result.fail("用户不存在");
        }

        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))){
            return Result.fail("密码不正确");
        }

        String jwt = jwtUtils.generateToken(user.getId());
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-control-Expose-Headers", "Authorization");

        return Result.succ(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("mobile", user.getMobile())
                .put("email", user.getEmail())
                .put("charge",user.getCharge())
                .put("userType",user.getUserType())
                .map()
        );
    }


}
