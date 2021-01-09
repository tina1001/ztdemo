package com.itheima.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.MyException;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zt
 * @Date:2021/1/8 20:24
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealDao setmealDao;
    /*
     * 新增套餐
     * 一：添加套餐
     * 二：添加检查组ids和套餐id到中间表
     */
    @Override
    public void addSetmeal(Setmeal setmeal, Integer[] checkgroupIds) {
        //添加套餐
        setmealDao.addSetmeal(setmeal);
        //添加检查组ids和套餐id到中间表
        Integer setmealId = setmeal.getId();
        addSetmealIdCheckgroupIds(checkgroupIds, setmealId);
    }
    /*
    * 本类中的公共方法：添加检查组ids和套餐id到中间表
    */
    private void addSetmealIdCheckgroupIds(Integer[] checkgroupIds, Integer setmealId) {
        if (checkgroupIds!=null && checkgroupIds.length>0) {
            for (Integer checkgroupId : checkgroupIds) {
                Map map = new HashMap();
                map.put("setmealId",setmealId);
                map.put("checkgroupId",checkgroupId);
                setmealDao.addSetmealIdCheckgroupIds(map);
            }
        }
    }

    /**
     * 查询套餐 分页
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
        Page<Setmeal> page = setmealDao.findPage(queryPageBean.getQueryString());

        //4. 返回总条数和当前页结果，封装到PageResult中
        return new PageResult(page.getTotal(),page.getResult());
    }
    /**
     * 删除套餐
     * 1.判断套餐与预约表关系是否存在,通过套餐id查询预约表
     * 2.删除套餐
     */
    @Override
    public void deleteSetmealById(Integer setmealId) {
        //判断套餐与预约表关系是否存在
        int count=setmealDao.findCountBySetmealId(setmealId);
        //不存在，先删除套餐与检查组表关系
        if (count>0) {
            throw new MyException("该套餐与预约表存在表关系，不能删除");
        }
        //删除套餐
        setmealDao.deleteSetmealById(setmealId);
    }
    /**
     * 编辑套餐
     * 1.根据套餐id,回显套餐信息+图片
     * 2.查询所有检查组列表数据
     * 3.回显检查组复选框
     * 4.修改套餐信息 /删除旧的套餐与检查组关系，添加套餐id检查组ids到中间表
     */
    @Override
    public Setmeal findSetmealById(int setmealId) {
        return setmealDao.findSetmealById(setmealId);
    }

    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(int setmealId) {
        return setmealDao.findCheckGroupIdsBySetmealId(setmealId);
    }

    @Override
    public void editSetmeal(Setmeal setmeal, Integer[] checkgroupIds) {
        //修改套餐信息
        setmealDao.editSetmeal(setmeal);
        //删除旧的套餐与检查组关系
        Integer setmealId = setmeal.getId();
        setmealDao.deleteSetmealIdCheckgroupIds(setmealId);
        // 添加套餐id检查组ids到中间表
        addSetmealIdCheckgroupIds(checkgroupIds, setmealId);
    }


}
