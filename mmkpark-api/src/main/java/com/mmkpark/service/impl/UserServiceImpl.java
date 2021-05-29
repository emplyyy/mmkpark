package com.mmkpark.service.impl;

import com.mmkpark.entity.User;
import com.mmkpark.mapper.UserMapper;
import com.mmkpark.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mumukang
 * @since 2021-04-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
