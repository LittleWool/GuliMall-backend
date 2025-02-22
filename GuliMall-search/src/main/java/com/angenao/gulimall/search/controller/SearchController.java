package com.angenao.gulimall.search.controller;

import com.angenao.gulimall.search.service.MallSearchService;
import com.angenao.gulimall.search.vo.SearchParam;
import com.angenao.gulimall.search.vo.SearchResult;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: SearchController
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/5 11:36
 * @Version: 1.0
 **/

@Controller
public class SearchController {
    @Autowired
    private MallSearchService mallSearchService;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    @GetMapping({"/list.html","/"})
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        SearchResult searchResult = mallSearchService.search(param);
        model.addAttribute("result",searchResult);
        return "listbak2";
    }

}
