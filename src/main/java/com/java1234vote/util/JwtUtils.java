package com.java1234vote.util;

import com.java1234vote.constant.SystemConstant;
import com.java1234vote.entity.CheckResult;
import io.jsonwebtoken.*;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 * jwt加密和解密的工具类
 * @author java1234_小锋
 * @site www.java1234.com
 * @company Java知识分享网
 * @create 2019-08-13 上午 10:06
 */
public class JwtUtils {

    /**
     * 创建JWT令牌
     * @param id 令牌ID，用于唯一标识令牌
     * @param subject 主题，可以是JSON数据，尽量保持简洁，用于描述令牌的内容
     * @param ttlMillis 令牌有效期（毫秒），指定令牌的过期时间
     * @return 生成的JWT令牌，包含了签名和声明信息
     */
    public static String createJWT(String id, String subject, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // 使用HS256算法进行签名
        long nowMillis = System.currentTimeMillis(); // 获取当前时间的毫秒数
        Date now = new Date(nowMillis); // 将当前时间转换为Date对象
        SecretKey secretKey = generalKey(); // 生成密钥
        JwtBuilder builder = Jwts.builder()
                .setId(id) // 设置令牌ID
                .setSubject(subject) // 设置令牌主题
                .setIssuer("Java1234") // 设置令牌签发者
                .setIssuedAt(now) // 设置令牌签发时间
                .signWith(signatureAlgorithm, secretKey); // 使用指定的签名算法和密钥进行签名
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis; // 计算令牌过期时间的毫秒数
            Date expDate = new Date(expMillis); // 将过期时间转换为Date对象
            builder.setExpiration(expDate); // 设置令牌过期时间
        }
        return builder.compact(); // 生成并返回JWT令牌的字符串表示形式
    }

    /**
     * 验证JWT
     * @param jwtStr JWT字符串
     * @return 验证结果
     */
    public static CheckResult validateJWT(String jwtStr) {
        //new一个JWT验证信息的实体对象
        CheckResult checkResult = new CheckResult();
        //Claims类的是表示JWT令牌中的声明信息。
        // Claims类提供了一组方法来获取和设置这些声明信息，使得在验证和处理JWT令牌时更加方便。
        Claims claims = null;
        try {
            // 解析JWT令牌，获取其中的声明信息
            claims = parseJWT(jwtStr);
            // 设置验证结果为成功，并将声明信息设置到验证结果中
            checkResult.setSuccess(true);
            checkResult.setClaims(claims);
        } catch (ExpiredJwtException e) {
            // JWT令牌过期异常，设置错误代码为JWT_ERRCODE_EXPIRE，验证结果为失败
            checkResult.setErrCode(SystemConstant.JWT_ERRCODE_EXPIRE);
            checkResult.setSuccess(false);
        } catch (SignatureException e) {
            // 签名验证失败异常，设置错误代码为JWT_ERRCODE_FAIL，验证结果为失败
            checkResult.setErrCode(SystemConstant.JWT_ERRCODE_FAIL);
            checkResult.setSuccess(false);
        } catch (Exception e) {
            // 其他异常，设置错误代码为JWT_ERRCODE_FAIL，验证结果为失败
            checkResult.setErrCode(SystemConstant.JWT_ERRCODE_FAIL);
            checkResult.setSuccess(false);
        }
        return checkResult;
    }

    /**
     * 生成加密Key
     * @return
     */
    public static SecretKey generalKey() {
        //从系统常量中获取Base64编码的密钥字符串，并解码为字节数组。
        byte[] encodedKey = Base64.decode(SystemConstant.JWT_SECERT);
        //使用解码后的字节数组生成AES算法的密钥。
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }


    /**
     * 解析JWT令牌，获取其中的声明信息
     * @param jwt JWT令牌
     * @return 解析后的声明信息
     * @throws Exception 解析过程中的异常
     */
    public static Claims parseJWT(String jwt) throws Exception {
        // 生成密钥
        SecretKey secretKey = generalKey();
        // 使用密钥解析JWT令牌，并获取其中的声明信息
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static void main(String[] args) throws InterruptedException {
        //小明失效 10s
        String sc = createJWT("1","小明", 60 * 60 * 1000);
        System.out.println(sc);
        System.out.println(validateJWT(sc).getErrCode());
        System.out.println(validateJWT(sc).getClaims().getId());
        System.out.println(validateJWT(sc).getClaims().getSubject());
        //Thread.sleep(3000);
        System.out.println(validateJWT(sc).getClaims());
        Claims claims = validateJWT(sc).getClaims();
        String sc2 = createJWT(claims.getId(),claims.getSubject(), SystemConstant.JWT_TTL);
        System.out.println(sc2);
    }

}
