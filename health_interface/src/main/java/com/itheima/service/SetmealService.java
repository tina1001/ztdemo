package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;

/**
 * @author zt
 * @Date:2021/1/8 20:25
 */
public interface SetmealService {
    void addSetmeal(Setmeal setmeal, Integer[] checkgroupIds);

    PageResult findPage(QueryPageBean queryPageBean);

    void deleteSetmealById(Integer setmealId);

    Setmeal findSetmealById(int setmealId);

    List<Integer> findCheckGroupIdsBySetmealId(int setmealId);

    void editSetmeal(Setmeal setmeal, Integer[] checkgroupIds);

    List<Setmeal> findAll();

    Setmeal findDetailById(int id);

    Setmeal findById(int id);
}
