package com.angenao.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.angenao.common.to.es.SkuEsModel;
import com.angenao.gulimall.search.Constant.EsConstant;
import com.angenao.gulimall.search.config.ElasticSearchConfig;
import com.angenao.gulimall.search.service.MallSearchService;
import com.angenao.gulimall.search.vo.SearchParam;
import com.angenao.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MallSearchServiceImpl
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/5 14:41
 * @Version: 1.0
 **/

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;


    @Override
    public SearchResult search(SearchParam searchParam) {

        //1.构建查询请求
        SearchRequest searchRequest = buidSearchRequest(searchParam);

        SearchResponse response=null;
        try {
            response = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //根据返回值构建查询结果
        SearchResult searchResult=buidSearchResult(searchParam,response);
        return searchResult;
    }



    private SearchRequest buidSearchRequest(SearchParam searchParam) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //使用searchSourceBuilder构建DSL
        /**
         * 模糊匹配过滤
         */
        //must-模糊匹配
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        searchSourceBuilder.query(boolQueryBuilder);

        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        //filter 过滤
        //1.分类id
        if (searchParam.getCatalog3Id()!=null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
        }
        //2.品牌id
        if (searchParam.getBrandId()!=null&&searchParam.getBrandId().size()>0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        //3.是否有库存
        if (searchParam.getHasStock()!=null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock()==1));
        }

        //4。价格范围
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            String skuPrice = searchParam.getSkuPrice();
            String[] priceRange = skuPrice.split("_");
            //需要进行空字符串排除
            List<String> skuPriceList = new ArrayList<>();
            for (String string : priceRange) {
                if (!StringUtils.isEmpty(string)) {
                    skuPriceList.add(string);
                }
            }
            if (skuPriceList.size()>0) {
                String[] s = new String[skuPriceList.size()];
                skuPriceList.toArray(s);

                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
                if (s.length==2) {
                    boolQueryBuilder.filter(rangeQuery.gte(s[0]).lte(s[1]));
                }else if (s.length==1) {
                    if (searchParam.getSkuPrice().startsWith("_")) {
                        //只有上限，没有下限
                        boolQueryBuilder.filter(rangeQuery.lte(s[0]));
                    }else {

                        //只有下限没上限
                        boolQueryBuilder.filter(rangeQuery.gte(s[0]));
                    }
                }
            }

        }

        //5.属性查找
        if (searchParam.getAttrs()!=null&&searchParam.getAttrs().size()>0) {

            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQueryBuilder=new NestedQueryBuilder("attrs",boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }

        }

        /**
         * 排序分页高亮
         */
        //排序
        if (!StringUtils.isEmpty(searchParam.getSort())) {
            String[] s = searchParam.getSort().split("_");
            SortOrder sortOrder=s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(s[0],sortOrder);
        }

        //分页
        searchParam.getPageNum();
        searchSourceBuilder.from((searchParam.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        //高亮
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder=new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        //品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg");
        brandAgg.field("brandId").size(50);
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brandAgg);
        //种类聚合
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("catelogAgg").field("catalogId").size(50);
        categoryAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(categoryAgg);
        //属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(50);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);

        searchSourceBuilder.aggregation(attrAgg);

        System.out.println(searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX},searchSourceBuilder);
        return searchRequest;
    }
    private SearchResult buidSearchResult(SearchParam searchParam,SearchResponse response) {

        SearchResult searchResult=new SearchResult();

        //查询到的所有商品信息
        List<SkuEsModel> skuEsModels=new ArrayList<>();
        SearchHits hits = response.getHits();
        if (hits.getHits()!=null&&hits.getHits().length>0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel skuEsModel= JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(searchParam.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String s = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(s);
                }
                skuEsModels.add(skuEsModel);
            }
        }
        searchResult.setProduct(skuEsModels);

         // 当前页码
        searchResult.setPageNum(searchParam.getPageNum());

        //总记录数
        long total = response.getHits().getTotalHits().value;
        searchResult.setTotal(total);



        //总页码
        long totalpages=total/EsConstant.PRODUCT_PAGESIZE;
        if (total%EsConstant.PRODUCT_PAGESIZE!=0) {
            totalpages++;
        }
        searchResult.setTotalPages((int)totalpages);
        //
        List<Integer> pageNavs=new ArrayList<>();
        for (int i = 1; i <=totalpages; i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //当前查询到的结果，所有涉及到的品牌
        List<SearchResult.BrandVo> brandVoList=new ArrayList<>();
        Map<Long,String> brandMap=new HashMap<>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brandAgg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo=new SearchResult.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));

            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");

            brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            brandMap.put(brandVo.getBrandId(),brandVo.getBrandName());
            brandVoList.add(brandVo);
        }
        searchResult.setBrands(brandVoList);

        //存放attr id和name映射
        Map<Long,String> attrsMap=new HashMap<>();
        //当前查询到的结果，所有涉及到的所有属性
        List<SearchResult.AttrVo> attrVoList=new ArrayList<>();
        ParsedNested attrAgg = response.getAggregations().get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo=new SearchResult.AttrVo();
            attrVo.setAttrId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            attrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            attrsMap.put(attrVo.getAttrId(), attrVo.getAttrName());
            List<String> attrValues = new ArrayList<>();
            if (attrValueAgg.getBuckets().size() > 0) {
                attrValueAgg.getBuckets().forEach(a -> attrValues.add(a.getKeyAsString()));
            }
            attrVo.setAttrValue(attrValues);
            attrVoList.add(attrVo);
            searchResult.getAttrIds().add(attrVo.getAttrId());
        }
        searchResult.setAttrs(attrVoList);




        //当前查询到的结果，所有涉及到的所有分类
        List<SearchResult.CatalogVo> catalogVoList=new ArrayList<>();
        ParsedLongTerms catelogAgg = response.getAggregations().get("catelogAgg");
        for (Terms.Bucket bucket : catelogAgg.getBuckets()) {
           SearchResult.CatalogVo catalogVo=new SearchResult.CatalogVo();
           catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms aggregation = bucket.getAggregations().get("catalogNameAgg");
            catalogVo.setCatalogName(aggregation.getBuckets().get(0).getKeyAsString());
            catalogVoList.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVoList);

        //面包屑导航
        List<SearchResult.NavVo> navVoList=new ArrayList<>();
        //获取查询条件
        List<String> attrs = searchParam.getAttrs();
        if (attrs!=null&&attrs.size()>0) {
            for (String attr : attrs) {
                SearchResult.NavVo navVo=new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavName(attrsMap.get(Long.parseLong(s[0])));
                navVo.setNavValue(s[1]);
                String encode=null;
                try {
                    encode = URLEncoder.encode(attr,"UTF-8");
                    encode=encode.replace("+", "%20");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                String address=searchParam.get_queryString().replace("&attrs="+encode,"");
                address=address.replace("attrs="+encode,"");
                navVo.setLink("http://search.gulimall.com/list.html?"+address);
                navVoList.add(navVo);
            }
        }

        if(searchParam.getBrandId()!=null&&searchParam.getBrandId().size()>0) {
            SearchResult.NavVo navVo=new SearchResult.NavVo();
            navVo.setNavName("品牌");
            navVo.setNavValue(brandMap.get(searchParam.getBrandId().get(0)));
            String encode=null;
            try {
                //todo 这里会空指针
                encode=URLEncoder.encode(brandMap.get(searchParam.getBrandId()),"UTF-8");
                encode=encode.replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String replace = searchParam.get_queryString().replace("&brandId=" + encode, "");
            replace = searchParam.get_queryString().replace("brandId=" + encode, "");

            navVo.setLink("http://search.gulimall.com/list.html"+replace);
        }


        searchResult.setNavs(navVoList);




        return searchResult;
    }
}
