package com.angenao.gulimall.member.dao;

import com.angenao.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:11:09
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
