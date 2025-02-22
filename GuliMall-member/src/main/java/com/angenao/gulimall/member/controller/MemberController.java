package com.angenao.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.angenao.common.constants.SessionKeyConstant;
import com.angenao.common.exception.BizCode;
import com.angenao.common.exception.PhoneExistException;
import com.angenao.common.exception.UserNameExistException;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.member.feign.CouponFeign;

import com.angenao.gulimall.member.to.SocialMemberTo;
import com.angenao.gulimall.member.vo.MemberLoginVo;
import com.angenao.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.member.entity.MemberEntity;
import com.angenao.gulimall.member.service.MemberService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;

import javax.servlet.http.HttpSession;


/**
 * 会员
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:11:09
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeign couponFeign;

//    @RequestMapping("coupons")
//    public R test(){
//        R coupons = couponFeign.coupons();
//
//        return R.ok().put("coupons", coupons.get("coupons"));
//
//    }

    /**
     * 用户注册
     * @param vo
     * @return
     */
    @PostMapping("/regist")
    public R register(@RequestBody MemberRegisterVo vo){
        try {
            memberService.register(vo);
        }catch (UserNameExistException userNameExistException){
            return R.error(BizCode.USER_EXIST_EXCEPTION.getCode(),BizCode.USER_EXIST_EXCEPTION.getMsg());
        }catch (PhoneExistException phoneExistException){
            return R.error(BizCode.PHONE_EXIST_EXCEPTION.getCode(), BizCode.PHONE_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo,HttpSession session){

        MemberEntity member=memberService.login(vo);
        MemberVo resultVo=new MemberVo();
        BeanUtils.copyProperties(member,resultVo);

        if(member!=null){
            return R.ok().put("data",resultVo);
        }else {
            return R.error(BizCode.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getCode(), BizCode.LOGIN_ACCOUNT_PASSWORD_EXCEPTION.getMsg());
        }

    }

    /**
     * 社交账户授权登陆
     */
    @PostMapping("/oath2/login")
    public R login(@RequestBody SocialMemberTo to){
      MemberVo member=  memberService.login(to);

      return R.ok().setData(member);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
  //  @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
