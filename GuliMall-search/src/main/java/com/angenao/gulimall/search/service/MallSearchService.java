package com.angenao.gulimall.search.service;

import com.angenao.gulimall.search.vo.SearchParam;
import com.angenao.gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;

/**
 * @ClassName: mallSearchService
 * @Description:
 * @Author: Little Wool
 * @Date: 2025/2/5 14:39
 * @Version: 1.0
 **/
@Service
public interface MallSearchService {

    /**
     * @param searchParam  检索条件(参数)
     * @return              检索结果
     */
    SearchResult search(SearchParam searchParam);
}
