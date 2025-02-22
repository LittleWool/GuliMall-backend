package com.angenao.gulimallware;

import com.angenao.common.to.SkuHasStockTo;
import com.angenao.gulimall.ware.GuliMallWareApplication;
import com.angenao.gulimall.ware.service.impl.WareSkuServiceImpl;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GuliMallWareApplication.class)
public class GuliMallWareApplicationTests {

	@Test
	public void contextLoads() {
		System.out.println();
		System.out.println(new Date());
		System.out.println();
	}

	@Autowired
	WareSkuServiceImpl wareSkuService;
	@Test
	public void hasStockTest() {
		List<Long> list = new ArrayList<>();
		list.add(11L);
		list.add(22L);
		list.add(2001L);
		list.add(2002L);
		list.add(29L);
		List<SkuHasStockTo> hastStock = wareSkuService.getHastStock(list);
		hastStock.forEach(System.out::println);

	}

}
