package com.itheima.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.exception.MyException;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* 预约控制层
 *
 * @author zt
 * @Date:2021/1/15 16:30
 */
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderSettingDao orderSettingDao;
    /*
     * 提交预约
     * (在controller中校验了验证码)
     */
    @Override
    @Transactional
    public Order submitOrder(Map<String, String> orderInfo) {
       /*
       {"setmealId":"12","sex":"1","name":"张三","telephone":"13311122211",
       "validateCode":"1111","idCard":"512501197203035172","orderDate":"2021-01-15"}
       */

        //获取请求参数
        String setmealId = orderInfo.get("setmealId");//套餐id
        String sex = orderInfo.get("sex");//sex
        String name = orderInfo.get("name");//name
        String telephone = orderInfo.get("telephone");//手机号
        String idCard = orderInfo.get("idCard");//idCard
        String orderType = orderInfo.get("orderType");//orderType（在controller中设置了）
        String orderDateStr = orderInfo.get("orderDate");//预约日期，并转为指定Date类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date orderDate = null;
        try {
            orderDate = sdf.parse(orderDateStr);
        } catch (ParseException e) {
            throw new MyException("日期格式不正确");
        }
        // 一：先判断当前日期是否可以预约？---------------------------------
        //通过预约日期,查询预约设置是否存在
        OrderSetting dbOrderSetting = orderSettingDao.findByOrderDate(orderDate);
        if(null != dbOrderSetting) {
            //二：存在，判断预约是否已满？-------------------------------------
            //数据库中已预约人数>=最大预约数,说明已约满，抛异常【所选日期预约已满】
            if(dbOrderSetting.getReservations() >= dbOrderSetting.getNumber()){
                throw new MyException("所选日期预约已满，请选其它日期");
            }
        }else {
            //不存在，抛异常【所选日期不能预约】
            throw new MyException("所选日期不能预约，请选其它日期");
        }
        //三：当前日期没有约满，再判断是否是会员？-------------------------------------
        //1.根据手机号码，查询会员是否存在
        Member member = memberDao.findByTelephone(telephone);
        Order order = new Order();
        //2.该会员不存在：添加会员（返回主键），设置会员信息
        if(null == member) {
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            member.setRemark("微信预约自动注册");
            member.setName(name);
            member.setSex(sex);
            member.setIdCard(idCard);
            member.setPassword(telephone.substring(5)); // 默认密码
            memberDao.addMember(member);
            //获取member的id，并设置到订单中
            order.setMemberId(member.getId());
        }else {
            //四：会员存在，再判断该会员是否重复预约？---------------------------------
            //1.将会员id, 预约日期，套餐id三个查询条件封装到order对象中，将order对象作为查询条件查询所有预约信息，返回orderList订单集合
            order.setMemberId(member.getId());//设置会员id
            order.setSetmealId(Integer.valueOf(setmealId));// 设置订单的套餐id
            order.setOrderDate(orderDate); // 设置订单预约日期
            List<Order> orderList = orderDao.findOrderByCondition(order);
            //orderList集合存在：抛异常【不能重复预约】
            if(!CollectionUtils.isEmpty(orderList)){   //if(null != orderList && orderList.size() > 0)
                throw new MyException("不能重复预约");
            }
        }
        //五：orderList集合不存在，则进行预约---------------------------------------
        //1.根据预约日期，更新已预约人数+1，防止超卖还需要已预约人数要小于可预约人数（行锁，更新成功返回1，失败返回0）
        int count = orderSettingDao.editReservationsByOrderDate(dbOrderSetting);
        if(count == 0){
            throw new MyException("所选日期预约已满，请选其它日期");
        }
        //2. 插入预约数据
        order.setOrderType(orderType);//设置预约类型
        order.setOrderStatus(Order.ORDERSTATUS_NO);//设置是否到诊
        orderDao.addOrder(order);
        //3.返回预约数据（前端页面跳转时需要order对象数据，所以要返回order）
        return order;
    }
    /**
     * 预约成功页面
     * 查询预约成功订单信息
     */
    @Override
    public Map<String, String> findDetailById(int id) {
        return orderDao.findOrderInfoById(id);
    }


}
