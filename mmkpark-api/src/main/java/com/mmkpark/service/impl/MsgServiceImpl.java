package com.mmkpark.service.impl;

import com.mmkpark.entity.Msg;
import com.mmkpark.mapper.MsgMapper;
import com.mmkpark.service.MsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mumukang
 * @since 2021-04-27
 */
@Service
public class MsgServiceImpl extends ServiceImpl<MsgMapper, Msg> implements MsgService {

}
