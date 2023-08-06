package com.java1234vote.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.java1234vote.constant.SystemConstant;
import com.java1234vote.entity.R;
import com.java1234vote.entity.WxUserInfo;
import com.java1234vote.properties.WeixinProperties;
import com.java1234vote.service.WxUserInfoService;
import com.java1234vote.util.DateUtil;
import com.java1234vote.util.HttpClientUtil;
import com.java1234vote.util.JwtUtils;
import com.java1234vote.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 微信用户Controller
 */
@RestController
@RequestMapping("/user")
public class WeixinUserController {

    @Autowired
    private WxUserInfoService wxUserInfoService;

    //实体映射
    @Autowired
    private WeixinProperties weixinProperties;

    //httpClient注入
    @Autowired
    private HttpClientUtil httpClientUtil;

    @Value("${userImagePath}")
    private String userImagePath;


    @RequestMapping("/test")
    public R test(){
        List<WxUserInfo> list = wxUserInfoService.list();
        HashMap<String,Object> result=new HashMap<>();
        result.put("list",list);
        return R.ok(result);
    }

    /**
     * 微信用户登陆
     * @param wxUserInfo
     * @return
     */
    @RequestMapping("/wxLogin")
    public R wxLogin(@RequestBody WxUserInfo wxUserInfo){
        /**
         * jscode2session拼接
         *
         * 这段代码是用来获取用户的会话信息的。具体解释如下：
         *  -  `weixinProperties.getJscode2sessionUrl()` ：获取微信小程序的jscode2session接口的URL。
         * -  `weixinProperties.getAppid()` ：获取微信小程序的appid。
         * -  `weixinProperties.getSecret()` ：获取微信小程序的密钥。
         * -  `wxUserInfo.getCode()` ：获取用户的登录凭证code。
         * -  `grant_type=authorization_code` ：授权类型为授权码模式。
         *  通过将上述参数拼接在一起，形成一个完整的URL，然后发送HTTP请求到该URL，
         *  即可获取用户的会话信息，包括用户的唯一标识openid、会话密钥session_key等。
         */
        String jscode2sessionUrl=weixinProperties.getJscode2sessionUrl()+"?appid="+weixinProperties.getAppid()+"&secret="+weixinProperties.getSecret()+"&js_code="+wxUserInfo.getCode()+"&grant_type=authorization_code";
        System.out.println("jscode2sessionUrl----:"+jscode2sessionUrl);
        String result = httpClientUtil.sendHttpGet(jscode2sessionUrl);//通过httpClientUtil工具类发送HTTP GET请求获取结果。
        System.out.println("reslut:"+result);
        //将请求的结果解析数据为json格式
        JSONObject jsonObject = JSON.parseObject(result);
        //通过解析后的数据获取openid
        String openid = jsonObject.get("openid").toString();
        //插入用户到数据库 不存在=》添加   存在=》更新
        WxUserInfo resultWxUserInfo = wxUserInfoService.getOne(new QueryWrapper<WxUserInfo>().eq("openid", openid));//根据openid查询当前用户是否存在
        if (resultWxUserInfo==null){//resultWxUserInfo为空，表示不存在
            System.out.println("用户不存在");
            wxUserInfo.setOpenid(openid);//设置openid
            wxUserInfo.setRegisterDate(new Date());//设置注册时间=当前日期
            wxUserInfo.setLastLoginDate(new Date());//设置最后登陆的日期
            wxUserInfoService.save(wxUserInfo);
        }else {
            System.out.println("用户已存在，执行跟新操作");
            resultWxUserInfo.setLastLoginDate(new Date());//更新用户信息
            wxUserInfoService.saveOrUpdate(resultWxUserInfo);
        }
        //status=1 代表此用户已经被封禁
        if (resultWxUserInfo!=null && resultWxUserInfo.getStatus().equals("1")){
            return R.error(400,"用户被封禁");
        }else {//说明该用户存在且账户正常
            //利用JWT生成Token
            //SystemConstant.JWT_TTL为过期时间
            String token = JwtUtils.createJWT(openid, wxUserInfo.getNickName(), SystemConstant.JWT_TTL);
            Map<String,Object> reslutMap=new HashMap<>();
            reslutMap.put("token",token);
            reslutMap.put("openid",openid);
            return R.ok(reslutMap);
        }
    }


    // 方法的参数使用了@RequestHeader注解，并没有指定注解的value属性。
    // 这意味着默认会将方法参数名（在这里是"token"）作为请求头的名称进行匹配。
    // 在方法体内部，首先打印了获取到的token值。然后，使用JwtUtils工具类对token进行验证和解析，获取其中的Claims（声明）对象。Claims对象包含了JWT中的各种声明信息。
    // 接下来，通过查询数据库，根据Claims中的id（在这里是openid）获取相应的WxUserInfo对象。将获取到的WxUserInfo对象放入一个Map中，并返回一个R对象（自定义的响应对象）。
    // 总之，这段代码通过@RequestHeader注解获取HTTP请求头中的token值，并使用JWT工具类对token进行验证和解析。然后根据解析出的声明信息，查询数据库获取相应的用户信息，并返回给调用方。

    //@RequestHeader注解用于获取HTTP请求头中的信息，并将其绑定到方法参数上
    @RequestMapping("/getUserInfo")
    public R getUserInfo(@RequestHeader String token){
        System.out.println("token:"+token);
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        System.out.println("openid="+claims.getId());
        WxUserInfo wxUserInfo = wxUserInfoService.getOne(new QueryWrapper<WxUserInfo>().eq("openid", claims.getId()));
        Map<String,Object> map=new HashMap<>();
        map.put("wxUserInfo",wxUserInfo);
        return R.ok(map);
    }


    @RequestMapping("/updateNickName")
    public R updateNickName(@RequestBody WxUserInfo wxUserInfo,@RequestHeader String token){
        if (StringUtil.isNotEmpty(wxUserInfo.getNickName())) {
            Claims claims = JwtUtils.validateJWT(token).getClaims();
            wxUserInfoService.update(new UpdateWrapper<WxUserInfo>().eq("openid",claims.getId()).set("nick_name",wxUserInfo.getNickName()));
        }
        return R.ok();
    }

    @RequestMapping("/updateUserImage")
    public Map<String,Object> updateUserImage(MultipartFile userImage,@RequestHeader String token) throws Exception{
        /**
         * MultipartFile是Spring框架中的一个接口，用于处理HTTP请求中的文件上传。它是对javax.servlet.http.Part接口的封装和扩展。
         *  MultipartFile接口提供了一系列方法来获取和操作上传的文件。常用的方法包括：
         *  1. getOriginalFilename()：获取上传文件的原始文件名。
         * 2. getSize()：获取上传文件的大小。
         * 3. getContentType()：获取上传文件的媒体类型。
         * 4. getInputStream()：获取上传文件的输入流，可以用于读取文件内容。
         * 5. transferTo()：将上传文件保存到指定的目标位置。
         *  MultipartFile接口可以作为Spring MVC控制器方法的参数，用于接收客户端上传的文件。在控制器方法中，可以通过@RequestParam注解将HTTP请求中的文件参数绑定到MultipartFile类型的方法参数上。
         *  使用MultipartFile接口可以方便地处理文件上传，包括获取文件名、大小、类型等信息，以及保存文件到服务器或进行其他操作。它简化了文件上传的处理过程，提供了更便捷的方式来处理文件上传功能。
         */
        System.out.println("图片名称："+userImage.getName());
        Map<String,Object> map=new HashMap<>();
        if (!userImage.isEmpty()){
            //获取原始名字
            String originalFilename = userImage.getOriginalFilename();
            //截取后缀名
            String substring = originalFilename.substring(originalFilename.lastIndexOf("."));//.jpg 等
            //获取当前时间设置为新名字
            String newFileName=DateUtil.getCurrentDateStr()+ substring;
            FileUtils.copyInputStreamToFile(userImage.getInputStream(),new File(userImagePath+newFileName));
            map.put("userImageFileName",newFileName);
            map.put("code",0);
            map.put("msg","上传成功");
            //更新数据库

            //严重token
            Claims claims = JwtUtils.validateJWT(token).getClaims();

            UpdateWrapper<WxUserInfo> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("openid",claims.getId())
                    .set("avatar_url",newFileName);
            wxUserInfoService.update(updateWrapper);


        }
        return map;
    }
}
