package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.exception.MyException;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/12 10:53
 */
@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;

    /*  批量存入预约数据
     * 上传excel文件，将表中的内容封装到List<OrderSetting>中，再将数据存入orderSetting表中
     *  1.通过预约日期，查询表中可预约人数是否存在
     *  2.插入数据
     *  3.根据预约日期更新可预约人数
     */
    @Override
    @Transactional
    public void batchOrderSetting(List<OrderSetting> orderSettingList) {
        //1. 判断List<Ordersetting>不为空
        if(!CollectionUtils.isEmpty(orderSettingList)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //2.  遍历Ordersetting集合，将数据插入orderSetting表
            for (OrderSetting orderSetting : orderSettingList) {
                //3. 通过预约日期，查询对应数据是否存在？
                OrderSetting dbOrderSetting = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
                //4. 不存在，插入数据；
                if (null == dbOrderSetting) {
                    orderSettingDao.addOrderSetting(orderSetting);
                } else {
                    // 5. 存在,再判断数据库中已预约人数，是否大于要插入的可预约人数
                    if (dbOrderSetting.getReservations() > orderSetting.getNumber()) {
                        // 大于则抛异常，接口方法 异常声明   //.format把Date型的字符串转换成特定格式的String类型
                        throw new MyException(simpleDateFormat.format(orderSetting.getOrderDate()) + ": 最大预约数不能小于已预约人数");
                    } else {
                        // 小于，则根据预约日期更新可预约人数
                        orderSettingDao.updateNumber(orderSetting);
                    }
                }
            }
        }
    }
    /*
     * [日历组件显示预约数据]: 通过月份查询预约设置信息
     *
     */
    @Override
    public List<Map<String, Integer>> getOrderSettingByMonth(String date) {
        date+="%"; //模糊查询
        return orderSettingDao.getOrderSettingByMonth(date);
    }
    /*
     * 通过日期，单个设置可预约的最大数
     * 和批量添加步骤一样：
     *  1.通过预约日期，查询表中可预约人数是否存在
     *  2.插入数据
     *  3.根据预约日期更新可预约人数
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) throws MyException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //1.通过预约的日期来查询预约设置表，判断该日期的数据是否存在？
        OrderSetting dbOrderSetting = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
        //2.不存在，添加记录
        if(null == dbOrderSetting) {
            orderSettingDao.addOrderSetting(orderSetting);
        }else {
            //3.存在，再判断数据库中已预约人数，是否大于要插入的可预约人数
            if(dbOrderSetting.getReservations() > orderSetting.getNumber()) {
                //大于， 抛异常，接口方法 异常声明
                throw new MyException(sdf.format(orderSetting.getOrderDate()) + ": 最大预约数不能小于已预约人数");
            } else {
                //小于，更新最大预约数
                orderSettingDao.updateNumber(orderSetting);
            }
        }
    }
}