package com.angenao.gulimall.member.service;

import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.member.to.SocialMemberTo;
import com.angenao.gulimall.member.vo.MemberLoginVo;
import com.angenao.gulimall.member.vo.MemberRegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angenao.common.utils.PageUtils;
import com.angenao.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:11:09
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo vo);


    MemberEntity login(MemberLoginVo vo);

    MemberVo login(SocialMemberTo socialMemberTo);
}

