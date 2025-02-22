package com.angenao.gulimall.search.service;

import com.angenao.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: ProductSaveService
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/14 16:15
 * @Version: 1.0
 **/

@Service
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
