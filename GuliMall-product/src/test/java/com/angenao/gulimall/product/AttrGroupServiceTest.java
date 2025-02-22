package com.angenao.gulimall.product;

import com.angenao.gulimall.product.dao.SkuSaleAttrValueDao;
import com.angenao.gulimall.product.service.AttrGroupService;
import com.angenao.gulimall.product.vo.SpuItemAttrGroupVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName: AttrGroupServiceTest
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/8 10:00
 * @Version: 1.0
 **/

@SpringBootTest
@RunWith(SpringRunner.class)
public class AttrGroupServiceTest {

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void getAttrGroupWithAttrsBySpuIdTest() {
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(14l, 225l);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    public void ggetSaleAttrBySpuIdTest(){
        System.out.println(skuSaleAttrValueDao.getSaleAttrBySpuId(14l));
    }
}
