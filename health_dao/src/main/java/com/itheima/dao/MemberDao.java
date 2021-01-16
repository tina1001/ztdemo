package com.itheima.dao;

import com.itheima.pojo.Member;

/**
 * @author zt
 * @Date:2021/1/15 17:02
 */
public interface MemberDao {
    /*
     * 根据手机号码，查询会员是否存在
     * 添加会员信息
     */
    Member findByTelephone(String telephone);

    void addMember(Member member);
}
