package com.itheima.controller;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiNiuUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author zt
 * @Date:2021/1/8 20:23
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    private static Logger log = LoggerFactory.getLogger(SetmealController.class);

    @Reference
    private SetmealService setmealService;
    /*
    * 图片上传
    */
    @PostMapping("/upload")
    public Result upload(MultipartFile imgFile){
        //一：生成唯一文件名
        //1.获取源文件名
        String originalFilename = imgFile.getOriginalFilename();
        //2.截取后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //3.生成唯一id
        String uId = UUID.randomUUID().toString();
        //4.拼接唯一文件名（2+3）
        String filename = uId + suffix;
        //二：调用七牛云工具上传图片
        try {
            QiNiuUtils.uploadViaByte(imgFile.getBytes(),filename);
            //三：返回数据(将需要返回的数据封装到map中)
            /*{
                imgName: 图片名 , 补全formData.img
                domain: 七牛的域名 图片回显imageUrl = domain+图片名
            }
            */
            Map<String,String> map = new HashMap<String,String>(2);
            map.put("imgName",filename);
            map.put("domain",QiNiuUtils.DOMAIN);
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,map);

        } catch (IOException e) {
            log.error("上传文件失败了",e);
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }
    /*
     * 新增套餐
     */
    @PostMapping("/addSetmeal")
    public Result addSetmeal( Integer[] checkgroupIds,@RequestBody Setmeal setmeal){
        // 调用服务添加套餐
        setmealService.addSetmeal(setmeal,checkgroupIds);
        return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
    }
    /**
     * 查询套餐 分页
     */
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return  setmealService.findPage(queryPageBean);
    }
    /**
     * 删除套餐
     * 1.判断套餐与预约表关系是否存在,通过套餐id查询预约表
     * 2.删除套餐
     */
    @PostMapping("/deleteSetmealById")
    public Result deleteSetmealById(int SetmealId){
        setmealService.deleteSetmealById(SetmealId);
        return new Result(true, "删除套餐成功");
    }
    /**
     * 编辑套餐
     * 1.根据套餐id,回显套餐信息+图片
     * 2.查询所有检查组列表数据(检查组控制层)
     * 3.回显检查组复选框
     * 4.修改套餐信息 /删除旧的套餐与检查组关系，添加套餐id检查组ids到中间表
     */
    @GetMapping("/findSetmealById")
    public Result findSetmealById(int setmealId){
        // 套餐信息
        Setmeal setmeal = setmealService.findSetmealById(setmealId);
        // 返回数据（套餐数据+域名）
        Map<String,Object> map = new HashMap<String,Object>(2);
        map.put("setmeal",setmeal);
        map.put("domain",QiNiuUtils.DOMAIN);
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,map);
    }
    @GetMapping("/findCheckGroupIdsBySetmealId")
    public Result findCheckGroupIdsBySetmealId(int setmealId){
        List<Integer> checkgroupIds = setmealService.findCheckGroupIdsBySetmealId(setmealId);
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS,checkgroupIds);
    }
    @PostMapping("/editSetmeal")
    public Result editSetmeal(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        // 调用服务修改套餐
        setmealService.editSetmeal(setmeal,checkgroupIds);
        return new Result(true, "编辑套餐成功");
    }
}
