package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;

/**
 * 检查项接口服务
 */
public interface CheckItemService {
    /**
     * 查询所有检查项（不分页）
     */
    List<CheckItem> findAllCheckitem();
    /**
     * 新增检查项
     */
    void addCheckItem(CheckItem checkItem);
    /**
     * 查询所有检查项（分页）
     */
    PageResult findPage(QueryPageBean queryPageBean);
    /*
     * 编辑检查项
     *
     * 一：根据id回显该检查项数据
     * 二：修改检查项数据
     */
    CheckItem findCheckitemById(Integer checkItemId);

    void editCheckItem(CheckItem checkItem);
    /*
     * 删除检查项
     */
    void deleteCheckitemById(Integer checkItemId);
}
