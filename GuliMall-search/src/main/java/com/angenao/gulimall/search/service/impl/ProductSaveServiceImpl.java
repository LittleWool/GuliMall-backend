package com.angenao.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.angenao.common.to.es.SkuEsModel;
import com.angenao.gulimall.search.Constant.EsConstant;
import com.angenao.gulimall.search.config.ElasticSearchConfig;
import com.angenao.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: ProductSaveServiceImpl
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/14 16:17
 * @Version: 1.0
 **/

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

       BulkRequest bulkRequest = new BulkRequest();
       skuEsModels.forEach(skuEsModel -> {
           IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
           indexRequest.id(skuEsModel.getSkuId().toString());
           String jsonString = JSON.toJSONString(skuEsModel);
           indexRequest.source(jsonString, XContentType.JSON);
           bulkRequest.add(indexRequest);
       });
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        boolean b = !bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成{}", collect);
        return b;
    }
}
