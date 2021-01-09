package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/8 1:55
 */
public interface CheckGroupDao {
    /*
     * 新增 检查组
     *
     * 一：查询所有检查项列表数据
     * 二：插入中间表(检查组id和对应的检查项ids)
     * 三：插入检查组表(添加后查询检查组id)
     */
    void addCheckgroup(CheckGroup checkGroup);

    void addCheckGroupIdAndCheckitemId(Map map);
    /*
     * 查询所有检查组 （分页）
     */
    Page<CheckGroup> findPage(String queryString);

    /*
     * 编辑 检查组
     * 一：根据id查询检查组信息
     * 二：查询所有检查项列表数据
     * 三：回显检查项列表复选框
     * 四：插入中间表(删除旧的添加新的) / 插入检查组表
     */
    CheckGroup findCheckgroupById(Integer checkgroupId);

    List<Integer> findCheckitemIdsByGroupId(Integer checkgroupId);

    void editCheckgroup(CheckGroup checkGroup);

    void deleteCheckItemIdsAndCheckGroupId(Integer checkGroupId);

    /*
     * 删除 检查组
     * 1通过检查组id，查询（检查组与套餐）是否存在表关系
     * 2通过检查组id，删除检查组信息
     */
    int selectCheckGroupIdCount(Integer checkGroupId);

    void deleteCheckGroupIdById(Integer checkGroupId);
    /*
     *查询所有 检查组 不分页
     */
    List<CheckGroup> findAllCheckgroup();

}
