package com.itheima.health.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zt
 * @Date:2021/1/11 18:03
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealMobileController {

    @Reference
    private SetmealService setmealService;

    /*
    * 查询所有套餐  不分页
    */
    @GetMapping("/getSetmeal")
    public Result getSetmeal(){
        // 查询所有的套餐
        List<Setmeal> setmealList = setmealService.findAll();
        // 遍历套餐集合；拼接图片全路径，设置到套餐img属性中
        setmealList.forEach(s->{
            s.setImg(QiNiuUtils.DOMAIN + s.getImg());
        });
        return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS,setmealList);
    }
    /*
    * 查询套餐详情
    */
    @GetMapping("/findDetailById")
    public Result findDetailById(int id){
        // 调用服务查询详情
        Setmeal setmeal = setmealService.findDetailById(id);
        // 设置图片的完整路径
        setmeal.setImg(QiNiuUtils.DOMAIN + setmeal.getImg());
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }


}
