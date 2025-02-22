package com.angenao.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angenao.gulimall.product.entity.AttrEntity;
import com.angenao.gulimall.product.service.AttrAttrgroupRelationService;
import com.angenao.gulimall.product.service.CategoryService;
import com.angenao.gulimall.product.service.impl.AttrServiceImpl;
import com.angenao.gulimall.product.vo.AttrGroupRelationVo;
import com.angenao.gulimall.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angenao.gulimall.product.entity.AttrGroupEntity;
import com.angenao.gulimall.product.service.AttrGroupService;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.R;



/**
 * 属性分组
 *
 * @author LittleWool
 * @email a996649790@gmail.com
 * @date 2024-11-14 12:26:45
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrServiceImpl attrService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
   // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,@PathVariable Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);
//        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 删除关联
     * @param attrGroupRelationVos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos){
        attrGroupService.deleteAttrGroupRealtions(attrGroupRelationVos);
        return R.ok();
    }

    @PostMapping("/attr/relation")
    public R saveBatchRelations(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos){
        attrAttrgroupRelationService.saveBathRelations(attrGroupRelationVos);
        return R.ok();
    }

    /**
     * 获取分类下所有分组&关联属性
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable Long catelogId){
        List<AttrGroupWithAttrsVO> attrGroupWithAttrsVOS=attrGroupService.getAttrGroupWithAttrs(catelogId);
        return R.ok().put("data", attrGroupWithAttrsVOS);
    }

    /**
     * 查询在该分类下，还未被分组关联的属性
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,@PathVariable Long attrgroupId){
      PageUtils pageUtils=attrService.getNoRalations(params,attrgroupId);
      return R.ok().put("page", pageUtils);
    }

    /**
     * 查询分组所有属性
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getRelations(@PathVariable Long attrgroupId){
       List<AttrEntity> attrEntities=attrService.getAttrGroupAttrs(attrgroupId);
        return R.ok().put("data", attrEntities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
  //  @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        attrGroup.setCatelogPath(categoryService.findLongPath(attrGroup.getCatelogId()));
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroupEntity){
		attrGroupService.save(attrGroupEntity);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
