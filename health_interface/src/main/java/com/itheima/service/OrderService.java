package com.itheima.service;

import com.itheima.exception.MyException;
import com.itheima.pojo.Order;

import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/15 16:29
 */
public interface OrderService {
    Order submitOrder(Map<String, String> orderInfo) throws MyException;

    Map<String, String> findDetailById(int id);
}
