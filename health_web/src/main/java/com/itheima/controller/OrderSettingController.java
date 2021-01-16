package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zt
 * @Date:2021/1/11 20:08
 *
 * 【流式编程】示例：获取一个List中，元素不为null的个数
 *  List<Integer> nums = Lists.newArrayList(1,null,3,4,null,6);
 *  nums.stream().filter(num -> num != null).count();
 *
 */
@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {
    private static final Logger log = LoggerFactory.getLogger(OrderSettingController.class);

    @Reference
    private OrderSettingService orderSettingService;

    /*
     * 上传excel文件，将表中的内容封装到List<OrderSetting>中，再将数据存入orderSetting表中
     *
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile excelFile) {
        try {
            //1. 解析excel文件，调用POIUitls解析文件，得到List<String[]>字符串数组集合 ，每一个数组就代表着一行记录
            List<String[]> strings = POIUtils.readExcel(excelFile);
            log.debug("导入预约设置读取到了{}条记录",strings.size());

            //2. 转成List<Ordersetting>
            //①遍历该集合，得到字符串数组 (String[] 长度为2, 0:日期，1：数量)
            //②将字符串数组转为OrderSetting对象,就要创建OrderSetting对象；
            //③将数组下标为0和1的字符串分别设置到OrderSetting对象中（注意两个字符串分别转为日期类型和int类型）
            //④将orderSetting结果放到orderSetting集合中
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(POIUtils.DATE_FORMAT); //"yyyy/MM/dd"

            List<OrderSetting> orderSettingList = strings.stream().map(arr -> {
                OrderSetting orderSetting = new OrderSetting();
                try {
                    orderSetting.setOrderDate(simpleDateFormat.parse(arr[0]));  //String -> Date
                    orderSetting.setNumber(Integer.valueOf(arr[1]));    //String -> int
                } catch (ParseException e) {
                }
                return orderSetting;
                 }).collect(Collectors.toList());
            //3. 调用service方法做导入，返回给页面
            orderSettingService.batchOrderSetting(orderSettingList);
            return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);

        } catch (IOException e) {
            log.error("导入预约设置失败",e);
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
        }
    /*
     * [日历组件显示预约数据]: 通过月份查询预约设置信息
     *
     */
    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){
        List<Map<String,Integer>> data = orderSettingService.getOrderSettingByMonth(date);
        return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS,data);
    }

    /*
     * 通过日期，设置可预约的最大数
     */
    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        orderSettingService.editNumberByDate(orderSetting);
        return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    }
    }

