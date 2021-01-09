package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查项控制层
 */

@RestController  //@Controller + @ResponseBody
@RequestMapping("/checkitem")
public class CheckItemController {
    @Reference
    private CheckItemService checkItemService;

    /*
     * 查询所有检查项 （不分页）
     */
    @RequestMapping(value = "/findAllCheckitem",method = RequestMethod.GET)
    public Result findAllCheckitem(){
            List<CheckItem> checkItemList =  checkItemService.findAllCheckitem();
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemList);
    }
    /*
     * 新增检查项
     */
    @RequestMapping(value = "/addCheckItem",method = RequestMethod.POST)
    public Result addCheckItem(@RequestBody CheckItem checkItem ){
            checkItemService.addCheckItem(checkItem);
            return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
    }
    /*
     * 查询所有检查项 （分页）
     *
     * 注意:这里直接返回pageResult,不提示消息，提醒太多了很烦
     */
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
            return checkItemService.findPage(queryPageBean);
    }
    /*
     * 编辑检查项
     *
     * 一：根据id回显该检查项数据
     * 二：修改检查项数据
     */
    @GetMapping("/findCheckitemById")
    public Result findCheckitemById(Integer checkItemId){
            CheckItem checkItem= checkItemService.findCheckitemById(checkItemId);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
    }

    @PostMapping("/editCheckItem")
    public Result editCheckItem(@RequestBody CheckItem checkItem ){
            checkItemService.editCheckItem(checkItem);
            return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
    }
    /*
     * 删除检查项
     * 一：根据检查项id查询，检查项id在中间表中的数量
     * 二：根据id删除检查项
     */
    @GetMapping("/deleteCheckitemById")
    public Result deleteCheckitemById(Integer checkItemId){
            checkItemService.deleteCheckitemById(checkItemId);
            return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }
}
