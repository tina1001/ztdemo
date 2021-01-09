package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;

import java.util.List;

/**
 * 持久层接口
 */
public interface CheckItemDao {
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
    Page<CheckItem> findPage(String queryString);
    /*
     * 编辑检查项
     * 一：根据id回显该检查项数据
     * 二：修改检查项数据
     */
    CheckItem findCheckitemById(Integer checkItemId);

    void editCheckItem(CheckItem checkItem);
    /*
     * 删除检查项
     * 一：根据检查项id查询，检查项id在中间表中的数量
     * 二：根据id删除检查项
     */
    int findCountByCheckItemId(Integer checkItemId);

    void deleteCheckitemById(Integer checkItemId);


}
