package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/13 10:54
 */
public interface OrderSettingDao {
    /*  批量存入预约数据
     * 上传excel文件，将表中的内容封装到List<OrderSetting>中，再将数据存入orderSetting表中
     *  1.通过预约日期，查询表中可预约人数是否存在
     *  2.插入数据
     *  3.根据预约日期更新可预约人数
     */
    OrderSetting findByOrderDate(Date orderDate);

    void addOrderSetting(OrderSetting orderSetting);

    void updateNumber(OrderSetting orderSetting);
    /*
     * [日历组件显示预约数据]: 通过月份查询预约设置信息
     */
    List<Map<String, Integer>> getOrderSettingByMonth(String date);
    /*
     * 根据预约日期，更新已预约人数+1
     */
    int editReservationsByOrderDate(OrderSetting dbOrderSetting);
}
