package com.itheima.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项服务实现类
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional //2.6.2
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    //查询所有检查项（不分页）
    @Override
    public List<CheckItem> findAllCheckitem() {
        return checkItemDao.findAllCheckitem();
    }
    /**
     * 新增检查项
     */
    @Override
    public void addCheckItem(CheckItem checkItem) {
        checkItemDao.addCheckItem(checkItem);
    }
    /**
     * 查询所有检查项（分页）
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //1. 用PageHelper设置分页(参数：当前页，每页条数)
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //2.对查询条件进行非空判断，不为空则给查询条件拼接% %（进行模糊查询）
        if (StringUtils.isNotEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        //3. 调用dao层的方法，执行sql语句（根据查询条件，查询所有检查项）。返回的当前页结果封装到插件的Page对象中
        //执行前，配置文件的sql语句已经与第一步的当前页，和每页条数拼接，先完成了分页
        Page<CheckItem> page = checkItemDao.findPage(queryPageBean.getQueryString());

        //4. 返回总条数和当前页结果，封装到PageResult中
        return new PageResult(page.getTotal(),page.getResult());
    }
    /*
     * 编辑检查项
     *
     * 一：根据id回显该检查项数据
     * 二：修改检查项数据
     */
    @Override
    public CheckItem findCheckitemById(Integer checkItemId) {
        return checkItemDao.findCheckitemById(checkItemId);
    }

    @Override
    public void editCheckItem(CheckItem checkItem) {
        checkItemDao.editCheckItem(checkItem);
    }
    /*
     * 删除检查项
     * 一：根据检查项id查询，检查项id在中间表中的数量
     * 二：根据id删除检查项
     */
    @Override
    public void deleteCheckitemById(Integer checkItemId) {
        //判断表关系是否存在（即查询检查项id在中间表中，是否存在）
        int count=checkItemDao.findCountByCheckItemId(checkItemId);
        if (count>0) {
            //存在，则该该检查项无法删除，抛异常
            throw new MyException(MessageConstant.DELETE_CHECKITEM_FAIL2);
        }
        //不存在，调用dao删除检查项
        checkItemDao.deleteCheckitemById(checkItemId);
    }


}
