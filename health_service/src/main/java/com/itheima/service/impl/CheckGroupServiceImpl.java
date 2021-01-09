package com.itheima.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/8 1:47
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    /*
     * 查询所有检查组 （分页）
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //1. 用PageHelper设置分页(参数：当前页，每页条数)
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //2.对查询条件进行非空判断，不为空则给查询条件拼接% %（进行模糊查询）
        if (StringUtils.isNotEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //3. 调用dao层的方法，执行sql语句。返回的当前页结果封装到插件的Page对象中
        //执行前，配置文件的sql语句已经与第一步的当前页，和每页条数拼接，先完成了分页
        Page<CheckGroup> page = checkGroupDao.findPage(queryPageBean.getQueryString());

        //4. 返回总条数和当前页结果，封装到PageResult中
        return new PageResult(page.getTotal(),page.getResult());
    }

    /*
     * 新增 检查组
     *
     * 一：查询所有检查项列表数据
     * 二：插入中间表(检查组id和对应的检查项ids)
     * 三：插入检查组表(添加后查询检查组id)
     */
    @Override
    public void addCheckgroup(Integer[] checkitemIds, CheckGroup checkGroup) {
        //对编码进行判断
        String code = checkGroup.getCode();
        if(StringUtils.isEmpty(code)){
            throw new MyException("编码不能为空");
        }
        if(code.length() > 32){
            throw new MyException("编码的长度不能超过32位");
        }
            //查询所有检查项列表数据（在检查项控制层）
        //1.插入检查组表(添加后查询检查组id)
        checkGroupDao.addCheckgroup(checkGroup);
        //2.插入中间表(检查组id和对应的检查项ids)
        // 获取检查组id
        Integer checkGroupId = checkGroup.getId();
        //调用本类中的公共方法，循环遍历，将检查项id和检查组id存入中间表
        addCheckItemIdsAndCheckGroupId(checkitemIds, checkGroupId);
    }
    /*
     * 本类中的公共方法，循环遍历，将检查项id和检查组id存入中间表
     */
    private void addCheckItemIdsAndCheckGroupId(Integer[] checkitemIds, Integer checkGroupId) {
        //判断用户是否勾选了检查项
        if (checkitemIds!=null && checkitemIds.length>0) {
            for (Integer checkitemId : checkitemIds) {
                //定义map将检查项id和检查组id存入
                Map map = new HashMap();
                map.put("checkGroupId",checkGroupId);
                map.put("checkitemId",checkitemId);
                //调用dao将map存入中间表
                checkGroupDao.addCheckGroupIdAndCheckitemId(map);
            }
        }
    }
    /*
     * 编辑 检查组
     * 一：根据id查询检查组信息
     * 二：查询所有检查项列表数据
     * 三：回显检查项列表复选框
     * 四：更新中间表 / 更新检查组表
     */
    @Override
    public CheckGroup findCheckgroupById(Integer checkgroupId) {
        return checkGroupDao.findCheckgroupById(checkgroupId);
    }

    @Override
    public List<Integer> findCheckitemIdsByGroupId(Integer checkgroupId) {
        return checkGroupDao.findCheckitemIdsByGroupId(checkgroupId);
    }

    @Override
    public void editCheckgroup(Integer[] checkItemIds, CheckGroup checkGroup) {
        //1. 调用dao更新检查组数据
        checkGroupDao.editCheckgroup(checkGroup);
        //2. 获取检查组id
        Integer checkGroupId = checkGroup.getId();
        //3. 先删除旧的表关系，即根据检查组id删除中间表的数据
        checkGroupDao.deleteCheckItemIdsAndCheckGroupId(checkGroupId);
        //4.根据检查组id 和检查项ids， 循环遍历往中间表中插入数据（调用本类中的公共方法）
        addCheckItemIdsAndCheckGroupId(checkItemIds, checkGroupId);
    }

    /*
     * 删除 检查组
     *
     */
    @Override
    public void deleteCheckGroupIdById(Integer checkGroupId) {
        //判断（检查组与套餐）是否存在表关系
        int count=checkGroupDao.selectCheckGroupIdCount(checkGroupId);
        if (count>0) {
            throw new MyException("该检查组与套餐存在表关系，不能删除");
        }
        //删除（检查组与检查项）表关系
        checkGroupDao.deleteCheckItemIdsAndCheckGroupId(checkGroupId);
        //通过检查组id，删除检查组信息
        checkGroupDao.deleteCheckGroupIdById(checkGroupId);
    }
    /*
    *查询所有 检查组 不分页
    */
    @Override
    public List<CheckGroup> findAllCheckgroup() {
        return checkGroupDao.findAllCheckgroup();
    }
}
