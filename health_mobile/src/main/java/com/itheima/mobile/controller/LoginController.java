package com.itheima.mobile.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/16 12:09
 * @Description 登录控制层
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;

    /**
     * @param
     * @return
     * @Description 手机号码快速登录（在验证码控制层发送了验证码）：
     * 校验验证码，判断redis中和请求参数中的验证码是否一致？
     * 判断是否为会员？
     * 添加cookie跟踪
     * 关闭资源，输出cookie和提示消息，给客户端
     */
    @PostMapping("/check")
    public Result login(@RequestBody Map<String, String> loginInfo, HttpServletResponse res) {
        //一：校验验证码，判断redis中和请求参数中的验证码是否一致？

        //1.拼接redis的key ；通过key获取redis中的验证码
        String telephone = loginInfo.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;

        Jedis jedis = jedisPool.getResource();
        String codeInRedis = jedis.get(key);
        log.info("codeInRedis:{}", codeInRedis);

        //2.判断redis中的验证码是否存在？------------------------
        if (StringUtils.isEmpty(codeInRedis)) {
            //不存在，提示【重新获取验证码】
            return new Result(false, "请重新获取验证码!");
        }
        log.info("codeFromUI:{}", loginInfo.get("validateCode"));

        //3.存在，校验前端提交过来的验证码是否相同？------------------------
        if (!codeInRedis.equals(loginInfo.get("validateCode"))) {
            // 不相同，提示【验证码错误】
            return new Result(false, "验证码不正确!");
        }
        //4.相同，删除key，防止重复提交
        jedis.del(key);

        //二： 通过手机号码判断是否为会员？---------------
        //1.通过手机号，查询会员信息是否存在
        Member member = memberService.findByTelephone(telephone);
        //2.不存在，不是会员就自动注册，添加会员信息
        if (null == member) {
            // 注册为会员
            member = new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            member.setRemark("快速登陆");
            memberService.addMember(member);
        }
        //3.是会员，添加cookie跟踪
        Cookie cookie = new Cookie("login_member_telephone", telephone);
        cookie.setMaxAge(30 * 24 * 60 * 60);// cookie存活时间
        cookie.setPath("/");// 所有的访问url都带上这个cookie
        //4.关闭资源，输出cookie和提示消息，给客户端
        jedis.close();
        res.addCookie(cookie);
        return new Result(true, MessageConstant.LOGIN_SUCCESS);
    }

}
