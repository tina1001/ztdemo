package com.itheima.mobile.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/15 16:21
 *
 * 体检预约 控制层
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;

    /*
     * 提交预约
     * (先发送验证码)
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo){

        Jedis jedis = jedisPool.getResource();

        // 一. 校验验证码
        // 1.拼接redis的key, 通过key获取redis中的验证码
        String telephone = orderInfo.get("telephone");
        String key = RedisMessageConstant.SENDTYPE_ORDER + "_" + telephone;
        String codeInRedis = jedis.get(key);

        log.info("codeInRedis:{}",codeInRedis);
        // 2.判断redis中的验证码是否存在；不存在：提示重新获取验证码
        if(StringUtils.isEmpty(codeInRedis)) {
            return new Result(false, "请重新获取验证码!");
        }
        log.info("codeFromUI:{}",orderInfo.get("validateCode"));
        //3.存在：校验前端提交过来的验证码，与redis中的是否相同；不相同，提交验证码错误
        if(!codeInRedis.equals(orderInfo.get("validateCode"))) {
            return new Result(false, "验证码不正确!");
        }
        //4. 相同，删除key, 防止重复提交
        jedis.del(key);

        // 二.调用预约服务
        //1.设置预约的类型为“微信预约”
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
        //2.调用预约服务
        Order order = orderService.submitOrder(orderInfo);
        //3.返回结果给页面，返回订单信息
        return new Result(true, MessageConstant.ORDER_SUCCESS,order);
    }

    /**
     * 预约成功页面
     * 查询预约成功订单信息
     */
    @GetMapping("/findById")
    public Result findById(int id){
        Map<String,String> orderInfo = orderService.findDetailById(id);
        return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
    }
