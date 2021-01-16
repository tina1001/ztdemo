package com.itheima.dao;

import com.itheima.pojo.Order;

import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/15 17:01
 */
public interface OrderDao {
    /*
     * 将会员id, 预约日期，套餐id三个查询条件封装到order对象中，将order对象作为查询条件查询所有预约信息
     *
     * 插入订单信息
     */
    List<Order> findOrderByCondition(Order order);

    void addOrder(Order order);
    /**
     * 预约成功页面
     * 查询预约成功订单信息
     */
    Map<String, String> findOrderInfoById(int id);
}
