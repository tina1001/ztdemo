package com.itheima.service;

import com.itheima.exception.MyException;
import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/12 8:59
 */
public interface OrderSettingService {

    void batchOrderSetting (List<OrderSetting> orderSettingList) throws MyException;

    List<Map<String, Integer>> getOrderSettingByMonth(String date);

    void editNumberByDate(OrderSetting orderSetting);
}
