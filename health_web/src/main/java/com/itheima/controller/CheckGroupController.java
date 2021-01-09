package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zt
 * @Date:2021/1/8 1:34
 *
 * 检查组控制层
 */

@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {
    @Reference
    private CheckGroupService checkGroupService;
    /*
     * 查询所有检查组 （分页）
     *
     * 注意:这里直接返回pageResult,不提示消息，提醒太多了很烦
     */
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return checkGroupService.findPage(queryPageBean);
    }
    /*
     * 新增 检查组
     *
     * 一：查询所有检查项列表数据
     * 二：插入中间表
     * 三：插入检查组表
     */
    @PostMapping("/addCheckgroup")
    public Result addCheckgroup(Integer[] checkitemIds, @RequestBody CheckGroup checkGroup){
        checkGroupService.addCheckgroup(checkitemIds,checkGroup);
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }
    /*
     * 编辑 检查组
     * 一：根据id查询检查组信息
     * 二：查询所有检查项列表数据(在检查项控制层)
     * 三：通过检查组id，回显检查项列表复选框
     * 四：更新中间表 / 更新检查组表
     */
    @GetMapping("/findCheckgroupById")
    public Result findCheckgroupById(Integer checkgroupId){
        CheckGroup checkGroup=checkGroupService.findCheckgroupById(checkgroupId);
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
    }
    //不提示消息直接返回检查项id集合
    @GetMapping("/findCheckitemIdsByGroupId")
    public List<Integer> findCheckitemIdsByGroupId(Integer checkgroupId){
        return checkGroupService.findCheckitemIdsByGroupId(checkgroupId);
    }
    @PostMapping("/editCheckgroup")
    public Result editCheckgroup(Integer[] checkItemIds,@RequestBody CheckGroup checkGroup ){
        checkGroupService.editCheckgroup(checkItemIds,checkGroup);
        return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }
    /*
     * 删除 检查组
     *
     */
    @GetMapping("/deleteCheckGroupById")
    public Result deleteCheckGroupIdById(Integer checkGroupId ){
        checkGroupService.deleteCheckGroupIdById(checkGroupId);
        return new Result(true, MessageConstant.DELETE_CHECKGROUP_SUCCESS);
    }
    /*
     * 查询所有 检查组（不分页）
     *
     */
    @GetMapping("/findAllCheckgroup")
    public Result findAllCheckgroup(){
        List<CheckGroup> checkGroupList= checkGroupService.findAllCheckgroup();
        return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroupList);
    }

}
