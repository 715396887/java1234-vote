package com.java1234vote.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.java1234vote.entity.R;
import com.java1234vote.entity.VoteDetail;
import com.java1234vote.entity.VoteItem;
import com.java1234vote.service.VoteDetailService;
import com.java1234vote.service.VoteItemService;
import com.java1234vote.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/voteDetail")
public class VoteDetailController {

    @Autowired
    private VoteDetailService voteDetailService;


    @Autowired
    private VoteItemService voteItemService;

    @RequestMapping("/add")
    @Transactional
    public R add(@RequestBody VoteDetail voteDetail, @RequestHeader String token){
        Map<String,Object> map=new HashMap<>();
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        String openid=claims.getId();//获取openid
        //一个投票 用户只能投一次
        int count = voteDetailService.count(new QueryWrapper<VoteDetail>().eq("openid", openid).eq("vote_id", voteDetail.getVoteId()));
        if (count>0){
            map.put("info","您已经投过了");
        }else {
            //更新投票数量
            voteItemService.update(new UpdateWrapper<VoteItem>().setSql("number=number+1").eq("id",voteDetail.getVoteItemId()));
            //添加
            voteDetail.setOpenid(openid);
            voteDetail.setVoteDate(new Date());
            voteDetailService.save(voteDetail);
            map.put("info","投票成功！");
        }
        return R.ok(map);
    }
}
