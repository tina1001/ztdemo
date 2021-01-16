package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/8 23:14
 */
public interface SetmealDao {
    /*
     * 新增套餐
     * 一：添加套餐
     * 二：添加检查组ids和套餐id到中间表
     */
    void addSetmeal(Setmeal setmeal);

    void addSetmealIdCheckgroupIds(Map map);
    /**
     * 查询套餐 分页
     */
    Page<Setmeal> findPage(String queryString);
    /**
     * 删除套餐
     * 1.判断套餐与预约表关系是否存在,通过套餐id查询预约表
     * 2.删除套餐
     */
    int findCountBySetmealId(Integer setmealId);

    void deleteSetmealById(Integer setmealId);
    /**
     * 编辑套餐
     * 1.根据套餐id,回显套餐信息+图片
     * 2.查询所有检查组列表数据
     * 3.回显检查组复选框
     * 4.修改套餐信息 /删除旧的套餐与检查组关系，添加套餐id检查组ids到中间表
     */
    Setmeal findSetmealById(int setmealId);

    List<Integer> findCheckGroupIdsBySetmealId(int setmealId);

    void editSetmeal(Setmeal setmeal);

    void deleteSetmealIdCheckgroupIds(Integer setmealId);
    /*
     * 查询所有套餐  不分页
     */
    List<Setmeal> findAll();
    /*
     * 查询套餐详情
     */
    Setmeal findDetailById(int id);
    /*
     * 通过id查询套餐信息(预约提交界面)
     */
    Setmeal findById(int id);
}
