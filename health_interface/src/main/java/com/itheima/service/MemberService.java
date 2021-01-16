package com.itheima.service;

import com.itheima.pojo.Member;

/**
 * @author zt
 * @Date:2021/1/16 12:12
 * @Description
 */
public interface MemberService {
    Member findByTelephone(String telephone);

    void addMember(Member member);
}
