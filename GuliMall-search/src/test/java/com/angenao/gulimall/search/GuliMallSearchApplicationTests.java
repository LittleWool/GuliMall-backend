package com.angenao.gulimall.search;


import com.alibaba.fastjson.JSON;
import com.angenao.gulimall.search.entity.Account;
import com.angenao.gulimall.search.entity.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GuliMallSearchApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        User user = new User();
        user.setId(1);
        user.setName("zhangsan");
        user.setAge(18);
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
    }


    @Test
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchSourceBuilder query = searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        searchRequest.source(query);

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(avgAggregationBuilder);

        System.out.println("检索条件"+searchSourceBuilder.toString());
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("返回结果"+response.toString());
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account:"+ account);
        }

    }

    @Test
   public void contextLoads() {
        System.out.println(restHighLevelClient);
    }


    @Test
    public void test001(){
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("789");
        String[] strs = new String[list.size()];
        list.toArray(strs);
        for (String str : strs) {
            System.out.println(str);
        }

    }

}
