package com.angenao.gulimall.member.service.impl;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.member.dao.MemberReceiveAddressDao;
import com.angenao.gulimall.member.entity.MemberReceiveAddressEntity;
import com.angenao.gulimall.member.service.MemberReceiveAddressService;


@Service("memberReceiveAddressService")
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity> implements MemberReceiveAddressService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberReceiveAddressEntity> page = this.page(
                new Query<MemberReceiveAddressEntity>().getPage(params),
                new QueryWrapper<MemberReceiveAddressEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberReceiveAddressEntity> getReceiveAddresses(long userId) {
        List<MemberReceiveAddressEntity> userReceiveAddresses = this.list(new QueryWrapper<MemberReceiveAddressEntity>().eq("member_id", userId));
        return userReceiveAddresses;
    }

    @Override
    public MemberReceiveAddressEntity getReceiveAddressByAddrId(long addrId) {
        MemberReceiveAddressEntity memberReceiveAddress = baseMapper.selectById(addrId);
        return memberReceiveAddress;
    }

}