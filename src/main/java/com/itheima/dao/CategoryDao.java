package com.itheima.dao;

import com.itheima.bean.Category;
import com.itheima.util.JdbcUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CategoryDao {

    JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
    public List<Category> queryAll() {

        List<Category> query = jdbcTemplate.query("select * from tab_category ORDER BY cid", new BeanPropertyRowMapper<>(Category.class));

        return query;

    }
}
