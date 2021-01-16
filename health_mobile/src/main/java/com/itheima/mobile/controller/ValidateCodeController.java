package com.itheima.mobile.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author zt
 * @Date:2021/1/15 15:16
 *
 * 验证码发送 控制层
 */
@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    private static final Logger log = LoggerFactory.getLogger(ValidateCodeController.class);

    @Autowired
    private JedisPool jedisPool;

    /*
     * 体检预约验证码
     */
    @PostMapping("/send4Order")
    public Result send4Order(String telephone){

        Jedis jedis = jedisPool.getResource();

        //1.判断redis中是否存在验证码
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        if (jedis.exists(key)) {
            //2.存在， 提示【已经发送，请注意查收】
            return new Result(false, "验证码已经发送过了，请注意查收");
        }
        //3.不存在，调用ValidateCodeUtils生成验证码，再调用SMSUtils发送验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        log.debug("========生成的验证码:{}",validateCode);

        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode+"");
        } catch (ClientException e) {
            log.error("发送验证码失败: {}:{}",telephone,validateCode);
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //4.将验证码存入redis，设置有效期
        jedis.setex(key,10*60,validateCode+"");
        //5.关闭资源，提示【发送验证码成功消息】
        jedis.close();
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
    /**
     * 登陆验证码
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        Jedis jedis = jedisPool.getResource();

        //1.判断redis中是否存在验证码（先从redis中获取验证码）
        String key = RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        if (jedis.exists(key)) {
            //2.如果存在，提示【验证码已发送，请注意查收】
            return new Result(false, "验证码已经发送过了，请注意查收");
        }
        //3.不存在，调用工具类生成验证码，调用SMSUtils发送验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        log.debug("========生成的验证码:{}",validateCode);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode+"");
        } catch (ClientException e) {
            //e.printStackTrace();
            log.error("发送验证码失败: {}:{}",telephone,validateCode);
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //4.将验证码存入redis，设置有效期
        jedis.setex(key,10*60,validateCode+"");
        //5.关闭资源，提示【发送验证码成功消息】
        jedis.close();
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }
}
