package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;

import java.util.List;

/**
 * @author zt
 * @Date:2021/1/8 1:43
 */
public interface CheckGroupService {
    void addCheckgroup(Integer[] checkitemIds, CheckGroup checkGroup);

    PageResult findPage(QueryPageBean queryPageBean);

    CheckGroup findCheckgroupById(Integer checkgroupId);

    List<Integer> findCheckitemIdsByGroupId(Integer checkgroupId);

    void editCheckgroup(Integer[] checkItemIds, CheckGroup checkGroup);

    void deleteCheckGroupIdById(Integer checkGroupId);

    List<CheckGroup> findAllCheckgroup();
}
