package com.angenao.gulimall.order;


import com.alibaba.fastjson.TypeReference;
import com.angenao.common.utils.R;
import com.angenao.gulimall.order.feign.ProductFeignService;
import com.angenao.gulimall.order.vo.SeckillSkuInfoVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GuliMallOrderApplicationTests {

    @Autowired
    ProductFeignService productFeignService;

    @Test
    public void contextLoads() {
        R r=productFeignService.info(44l);
        SeckillSkuInfoVo data = r.getData(new TypeReference<SeckillSkuInfoVo>() {
        });
        System.out.println(r);
        System.out.println(data);
    }

}
