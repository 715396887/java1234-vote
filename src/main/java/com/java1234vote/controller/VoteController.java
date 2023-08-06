package com.java1234vote.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234vote.entity.*;
import com.java1234vote.service.VoteDetailService;
import com.java1234vote.service.VoteItemService;
import com.java1234vote.service.VoteService;
import com.java1234vote.service.WxUserInfoService;
import com.java1234vote.util.DateUtil;
import com.java1234vote.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vote")
public class VoteController {

    @Value("${coverImagesFilePath}")
    private String coverImagesFilePath;

    @Value("${voteItemImagesFilePath}")
    private String voteItemImagesFilePath;

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteDetailService voteDetailService;

    @Autowired
    private VoteItemService voteItemService;

    @Autowired
    private WxUserInfoService wxUserInfoService;

    @RequestMapping("/uploadCoverImage")
    public Map<String,Object> uploadCoverImage(MultipartFile coverImage)throws Exception{
        System.out.println("请求到了");
        System.out.println("file"+coverImage.getName());
        Map<String,Object> map=new HashMap<>();
        if(!coverImage.isEmpty()){//不为空，则代表上传了文件
            System.out.println("请求到了");
            String originalFilename = coverImage.getOriginalFilename();//获取上传的文件名
            String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));//获取后缀名
            //创建新用户名
            String newFileName=DateUtil.getCurrentDateStr()+ suffixName;
            //存储到本地
            FileUtils.copyInputStreamToFile(coverImage.getInputStream(),new File(coverImagesFilePath+newFileName));
            map.put("code",0);
            map.put("msg","上传成功");
            map.put("coverImageFileName",newFileName);
        }
        return map;
    }

    @RequestMapping("/add")
    @Transactional
    public R add(@RequestBody Vote vote, @RequestHeader String token){
        //验证token
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        vote.setOpenid(claims.getId());//getid可以获取openid
        voteService.save(vote);
        List<VoteItem> voteItemList = vote.getVoteItemList();
        System.out.println("vote.getId():"+vote.getId());
        for (VoteItem voteItem:voteItemList){
            voteItem.setVoteId(vote.getId());
            voteItem.setNumber(0);
            voteItemService.save(voteItem);
        }
        return R.ok();
    }
    /**
     * 上传投票选项图片
     * @param voteItemImage
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadVoteItemImage")
    public Map<String,Object> uploadVoteItemImage(MultipartFile voteItemImage)throws
            Exception{
        System.out.println("filename:"+voteItemImage.getName());
        Map<String,Object> resultMap=new HashMap<>();
        if(!voteItemImage.isEmpty()){
        // 获取文件名
            String originalFilename = voteItemImage.getOriginalFilename();
            String suffixName=originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(voteItemImage.getInputStream(),new
                    File(voteItemImagesFilePath+newFileName));
            resultMap.put("code",0);
            resultMap.put("msg","上传成功");
            resultMap.put("voteItemImageFileName",newFileName);
        }
        return resultMap;
    }

    @RequestMapping("/listOrder")
    public R listOrder(@RequestHeader String token){
        Map<String,Object> map=new HashMap<>();
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        List<Vote> voteList = voteService.list(new QueryWrapper<Vote>().eq("openid", claims.getId()).orderByDesc("vote_end_time"));
        map.put("voteList",voteList);
        return R.ok(map);
    }

    /**
     * 根据id查询投票详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R findById(@PathVariable(value = "id")Integer id){
        Map<String,Object> map=new HashMap<>();
        Vote vote = voteService.getById(id);//获得投票信息
        WxUserInfo wxUserInfo = wxUserInfoService.getOne(new QueryWrapper<WxUserInfo>().eq("openid", vote.getOpenid()));//根据openid获取微信用户信息
        vote.setWxUserInfo(wxUserInfo);
        List<VoteItem> itemList = voteItemService.list(new QueryWrapper<VoteItem>().eq("vote_id", vote.getId()));
        vote.setVoteItemList(itemList);
        map.put("vote",vote);
        return R.ok(map);
    }

    /**
     * 获取指定用户参与的投票
     * @return
     */
    @RequestMapping("/listOfJoinUser")
    public R listOfJoinUser(@RequestHeader String token){
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        //嵌套子查询
        List<Vote> voteList = voteService.list(new QueryWrapper<Vote>().inSql("id", "SELECT vote_id FROM t_vote_detail WHERE openid='" + claims.getId() + "'").orderByDesc("vote_end_time"));
        Map<String, Object> map=new HashMap<>();
        map.put("voteList",voteList);
        return R.ok(map);
    }

    @RequestMapping("/delete/{id}")
    @Transactional
    public R deleteVote(@PathVariable(value = "id")Integer id){
        voteItemService.remove(new QueryWrapper<VoteItem>().eq("vote_id",id));
        voteDetailService.remove(new QueryWrapper<VoteDetail>().eq("vote_id",id));
        voteService.removeById(id);
        return R.ok();
    }
}
