package com.angenao.gulimall.product.web;

import com.angenao.common.constants.SessionKeyConstant;
import com.angenao.common.utils.R;
import com.angenao.common.vo.MemberVo;
import com.angenao.gulimall.product.entity.CategoryEntity;
import com.angenao.gulimall.product.service.CategoryService;
import com.angenao.gulimall.product.service.impl.CategoryServiceImpl;
import com.angenao.gulimall.product.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: IndexController
 * @Description:
 * @Author: Little Wool
 * @Date: 2024/12/15 23:34
 * @Version: 1.0
 **/

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;



    @GetMapping({"/", "/index"})
    public String index(Model model, HttpSession session) {
        // TODO 查询所有分类
       List<CategoryEntity> categoryEntities=categoryService.getLevel1Categories();
       model.addAttribute("categories",categoryEntities);
        MemberVo attribute = (MemberVo) session.getAttribute(SessionKeyConstant.SESSION_USER_KEY);
        System.out.println("loginUser:"+attribute);
        return "index";
    }

    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String,List<Catalog2Vo>> getCategoryJson(){
        Map<String,List<Catalog2Vo>> categories=categoryService.getCategoriesTree();
        return categories;
    }
}
