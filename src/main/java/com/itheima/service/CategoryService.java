package com.itheima.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.bean.Category;
import com.itheima.dao.CategoryDao;
import com.itheima.util.JedisUtils;

import java.util.List;

public class CategoryService {

    private CategoryDao categoryDao = new CategoryDao();

    public String queryAll() throws JsonProcessingException {
        //优先从Redis缓存中查找分类信息
        String categories = JedisUtils.getCache("categories");

        //如果缓存中没有，在调用到，查询mysql
        if (categories == null || "".equals(categories)){
            //调用Dao，查询mysql，得到所有分类信息
            List<Category> list = categoryDao.queryAll();

            //把集合转换成json格式的字符串
            ObjectMapper objectMapper = new ObjectMapper();
            categories= objectMapper.writeValueAsString(list);

            //把分类的json字符串缓存到Redis里
            JedisUtils.setCache("categories",categories);

        }
        //返回去
        return categories;
    }
}
