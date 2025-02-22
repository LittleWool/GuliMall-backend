package com.angenao.gulimall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angenao.gulimall.member.entity.MemberReceiveAddressEntity;
import com.angenao.gulimall.member.service.MemberReceiveAddressService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * 会员收货地址
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 15:11:09
 */
@RestController
@RequestMapping("member/memberreceiveaddress")
public class MemberReceiveAddressController {
    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:memberreceiveaddress:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberReceiveAddressService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 当前用户登录地址
     */
    @RequestMapping("/user/{userId}")
    List<MemberReceiveAddressEntity> getReceiveAddresses(@PathVariable("userId") long userId){
        List<MemberReceiveAddressEntity> res = memberReceiveAddressService.getReceiveAddresses(userId);
        return res;
    };


    @RequestMapping("/{addrId}")
    R getReceiveAddressByAddrId(@PathVariable("addrId") long addrId){
        MemberReceiveAddressEntity memberReceiveAddress=memberReceiveAddressService.getReceiveAddressByAddrId(addrId);
        return R.ok().setData(memberReceiveAddress);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
  //  @RequiresPermissions("member:memberreceiveaddress:info")
    public R info(@PathVariable("id") Long id){
		MemberReceiveAddressEntity memberReceiveAddress = memberReceiveAddressService.getById(id);

        return R.ok().put("memberReceiveAddress", memberReceiveAddress);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:memberreceiveaddress:save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.save(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:memberreceiveaddress:update")
    public R update(@RequestBody MemberReceiveAddressEntity memberReceiveAddress){
		memberReceiveAddressService.updateById(memberReceiveAddress);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:memberreceiveaddress:delete")
    public R delete(@RequestBody Long[] ids){
		memberReceiveAddressService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
