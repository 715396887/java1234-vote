package com.java1234vote.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234vote.entity.VoteItem;
import com.java1234vote.mapper.VoteItemMapper;
import com.java1234vote.service.VoteItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 投票选项service实现类
 */
@Service("voteItemServiceImpl")
public class VoteItemServiceImpl extends ServiceImpl<VoteItemMapper, VoteItem> implements VoteItemService {
    @Autowired
    private VoteItemMapper voteItemMapper;
}
