package com.java1234vote.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234vote.entity.Vote;
import com.java1234vote.mapper.VoteMapper;
import com.java1234vote.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 投票service实现类
 */
@Service("voteServiceImpl")
public class VoteServiceImpl extends ServiceImpl<VoteMapper, Vote> implements VoteService {

    @Autowired
    private VoteMapper voteMapper;

}
