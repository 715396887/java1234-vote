package com.java1234vote.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234vote.entity.WxUserInfo;
import com.java1234vote.mapper.WxUserInfoMapper;
import com.java1234vote.service.WxUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.WatchService;

/**+
 * 微信用户serviceImpl
 */
@Service("wxUserInfoService")
public class WxUserInfoServiceImpl extends ServiceImpl<WxUserInfoMapper, WxUserInfo> implements WxUserInfoService {
    // 通过@Autowired注解，Spring容器会在运行时自动查找并注入一个匹配类型的WxUserInfoMapper对象，
    // 使得WxUserInfoServiceImpl类可以直接使用该对象进行数据库操作，而无需手动创建或获取该对象。
    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;
}
