package com.angenao.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.angenao.gulimall.product.constants.ProductRedisKey;
import com.angenao.gulimall.product.service.CategoryBrandRelationService;
import com.angenao.gulimall.product.vo.Catalog2Vo;
import org.hibernate.validator.internal.util.logging.Log_$logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angenao.common.utils.PageUtils;
import com.angenao.common.utils.Query;

import com.angenao.gulimall.product.dao.CategoryDao;
import com.angenao.gulimall.product.entity.CategoryEntity;
import com.angenao.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 1.查询所有分类
     * 2.拼成树结构
     */

    @Override
    public List<CategoryEntity> categoryListTree() {
        List<CategoryEntity> categories = baseMapper.selectList(null);
        List<CategoryEntity> firstCategory = categories.stream().filter(c -> c.getParentCid() == 0).sorted((a, b) -> {
            return (a.getSort() == null ? 0 : a.getSort()) - (b.getSort() == null ? 0 : b.getSort());
        }).collect(Collectors.toList());
        firstCategory.forEach(c -> {
            c.setChildren(getCategoryChildren(c, categories));
        });
        return firstCategory;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO 判断是否有其他的引用
        baseMapper.deleteBatchIds(list);
    }

    @Override
    public Long[] findLongPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        CategoryEntity categoryEntity = getById(catelogId);
        while (categoryEntity != null) {
            paths.add(categoryEntity.getCatId());
            Long parentId = categoryEntity.getParentCid();
            if (parentId != null) {
                categoryEntity = getById(parentId);
            }
        }
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }


    @Transactional
    @Override
    @CacheEvict(value ={"category"},allEntries = true)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        System.out.println("查询数据库：一级分类数据");
        List<CategoryEntity> parentCid = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return parentCid;
    }

    //查询类别树1
    @Override
    @Cacheable(value = {"category"},key = "#root.methodName")
    public Map<String, List<Catalog2Vo>> getCategoriesTree(){
        //TODO 未加入分布式锁

        //查出所有的类
        List<CategoryEntity> allCategories = list(new QueryWrapper<CategoryEntity>());
        //collectLevel2 key是二级分类id value是三级分类
        Map<String, List<Catalog2Vo.Catalog3Vo>> collectLevel2 = allCategories.stream()
                .filter(item -> item.getCatLevel() == 2)
                .collect(Collectors.toMap(item -> item.getCatId().toString(), categoryLevel2 -> {
                            List<Catalog2Vo.Catalog3Vo> collect = allCategories.stream().filter(item -> item.getCatLevel() == 3 && item.getParentCid() == categoryLevel2.getCatId()).map(categoryLevel3
                                    -> {
                                Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(categoryLevel2.getCatId().toString(), categoryLevel3.getCatId().toString(), categoryLevel3.getName().toString());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                            return collect;
                        }
                ));
        Map<String, List<Catalog2Vo>> collect1 = allCategories.stream().filter(item -> item.getParentCid() == 0).collect(Collectors.toMap(
                categoryLevel1 -> categoryLevel1.getCatId().toString(), categoryLevel1 -> {
                    List<Catalog2Vo> collect = allCategories.stream().filter(it -> it.getCatLevel() == 2 && it.getParentCid() == categoryLevel1.getCatId()).map(categoryLevel2 -> {
                        Catalog2Vo catalog2Vo = new Catalog2Vo(categoryLevel1.getCatId().toString(), categoryLevel2.getCatId().toString(), categoryLevel2.getName().toString(), collectLevel2.get(categoryLevel2.getCatId().toString()));
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                    return collect;
                }
        ));
        return collect1;
    }

    //查询类别树2
    public Map<String, List<Catalog2Vo>> getCategoriesTree2() {
        ValueOperations<String, String> opv = stringRedisTemplate.opsForValue();
        String s = opv.get(ProductRedisKey.CATEGORY_JSON_REDIS_KEY);
        if (StringUtils.isEmpty(s)) {
            //如果redis 中没有缓存，则查询数据库存入redis
            System.out.println("category类别树，redis缓存未命中"+"……………………………………………………");
            Map<String, List<Catalog2Vo>> categoriesTreeFromDB = getCategoriesTreeFromDBWithRedissionLock();
            String categories = JSON.toJSONString(categoriesTreeFromDB);
            opv.set(ProductRedisKey.CATEGORY_JSON_REDIS_KEY,categories);
            return categoriesTreeFromDB;
        }
        System.out.println("category类别树，redis缓存命中"+"……………………………………………………");
        Map<String, List<Catalog2Vo>> categoriesTree = JSON.parseObject(s, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
        return categoriesTree;
    }


    //对从数据库读写数据加一个锁
    public Map<String, List<Catalog2Vo>> getCategoriesTreeFromDBWithRedissionLock(){
        RLock lock = redissonClient.getLock(ProductRedisKey.CATEGORY_JSON_REDIS_KEY);
        lock.lock();
        Map<String, List<Catalog2Vo>> categoriesTreeFromDB=null;
        try {
            Thread.sleep(10000);
            categoriesTreeFromDB = getCategoriesTreeFromDB();
        }catch (Exception e){

        }finally {
            lock.unlock();
        }
        return categoriesTreeFromDB;
    }

    //没有缓存时查询数据库
    public Map<String, List<Catalog2Vo>> getCategoriesTreeFromDB() {

        //查出所有的类
        List<CategoryEntity> allCategories = list(new QueryWrapper<CategoryEntity>());
        //collectLevel2 key是二级分类id value是三级分类
        Map<String, List<Catalog2Vo.Catalog3Vo>> collectLevel2 = allCategories.stream()
                .filter(item -> item.getCatLevel() == 2)
                .collect(Collectors.toMap(item -> item.getCatId().toString(), categoryLevel2 -> {
                            List<Catalog2Vo.Catalog3Vo> collect = allCategories.stream().filter(item -> item.getCatLevel() == 3 && item.getParentCid() == categoryLevel2.getCatId()).map(categoryLevel3
                                    -> {
                                Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(categoryLevel2.getCatId().toString(), categoryLevel3.getCatId().toString(), categoryLevel3.getName().toString());
                                return catalog3Vo;
                            }).collect(Collectors.toList());
                            return collect;
                        }
                ));
        Map<String, List<Catalog2Vo>> collect1 = allCategories.stream().filter(item -> item.getParentCid() == 0).collect(Collectors.toMap(
                categoryLevel1 -> categoryLevel1.getCatId().toString(), categoryLevel1 -> {
                    List<Catalog2Vo> collect = allCategories.stream().filter(it -> it.getCatLevel() == 2 && it.getParentCid() == categoryLevel1.getCatId()).map(categoryLevel2 -> {
                        Catalog2Vo catalog2Vo = new Catalog2Vo(categoryLevel1.getCatId().toString(), categoryLevel2.getCatId().toString(), categoryLevel2.getName().toString(), collectLevel2.get(categoryLevel2.getCatId().toString()));
                        return catalog2Vo;
                    }).collect(Collectors.toList());
                    return collect;
                }
        ));
        return collect1;

    }


    private List<CategoryEntity> getCategoryChildren(CategoryEntity father, List<CategoryEntity> children) {
        return children.stream().filter(c -> c.getParentCid() == father.getCatId())
                .map(c -> {
                    c.setChildren(getCategoryChildren(c, children));
                    return c;
                }).sorted((a, b) -> {
                    return (a.getSort() == null ? 0 : a.getSort()) - (b.getSort() == null ? 0 : b.getSort());
                }).collect(Collectors.toList());
    }

}