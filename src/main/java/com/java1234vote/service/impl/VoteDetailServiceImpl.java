package com.java1234vote.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234vote.entity.VoteDetail;
import com.java1234vote.mapper.VoteDetailMapper;
import com.java1234vote.service.VoteDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("voteDetailServiceImpl")
public class VoteDetailServiceImpl extends ServiceImpl<VoteDetailMapper, VoteDetail> implements VoteDetailService {
    @Autowired
    private VoteDetailMapper voteDetailMapper;
}
