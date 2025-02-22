package com.angenao.gulimall.member.service.impl;

import com.angenao.common.exception.PhoneExistException;
import com.angenao.common.exception.UserNameExistException;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.member.constant.MemberConstant;
import com.angenao.gulimall.member.entity.MemberLevelEntity;
import com.angenao.gulimall.member.to.SocialMemberTo;
import com.angenao.gulimall.member.vo.MemberLoginVo;
import com.angenao.gulimall.member.vo.MemberRegisterVo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.member.dao.MemberDao;
import com.angenao.gulimall.member.entity.MemberEntity;
import com.angenao.gulimall.member.service.MemberService;




@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelServiceImpl memberLevelService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo vo) {

        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = new MemberEntity();

        //设置默认会员等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        //检查手机号用户名是否被使用
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        //用户信息赋值
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());

        //给密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(passwordEncoder.encode(vo.getPassword()));


        memberDao.insert(memberEntity);

    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity member = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                        .eq("username", vo.getLoginacct())
                        .or().
                        eq("mobile", vo.getLoginacct()));
        if (member == null) {
            return null;
        }else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(vo.getPassword(), member.getPassword());
            if (matches) {
                return member;
            }else {
                return null;
            }
        }
    }

    @Override
    public MemberVo login(SocialMemberTo socialMemberTo) {
        //查询用户是否存在
        MemberDao memberDao = this.baseMapper;
        MemberEntity member = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("social_id", socialMemberTo.getId()));
        if (member == null) {
            member = new MemberEntity();
            member.setUsername(socialMemberTo.getLogin());
            member.setNickname(socialMemberTo.getName());
            member.setEmail(socialMemberTo.getEmail());
            member.setSocialId(socialMemberTo.getId());
            member.setLevelId(memberLevelService.getDefaultLevel().getId());
            memberDao.insert(member);
        }else {
            //将token放入redis
            stringRedisTemplate.opsForValue().set(
                    MemberConstant.OAUTH_GITEE_ACCESS_TOKEN_prefix+socialMemberTo.getId(),
                    socialMemberTo.getAccessToken(),
                    socialMemberTo.getExpiresIn(),
                    TimeUnit.SECONDS);
            memberDao.updateById(member);
        }
        MemberVo vo = new MemberVo();
        BeanUtils.copyProperties(member, vo);
        return vo;


    }

    private void checkUserNameUnique(String userName)throws UserNameExistException {
        int count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(count>0){
            throw new UserNameExistException();
        }
    }

    private void checkPhoneUnique(String phone)throws PhoneExistException {
        int count= baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count>0){
            throw new PhoneExistException();
        }

    }



}