package com.haozhikeji.dao;

import com.haozhikeji.bean.User;
import com.haozhikeji.util.JdbcUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDao {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());

    /*
    *
    * 用户注册
    *
    * */
    public int add(User user) {
        String sql = "INSERT into tab_user (uid,username,password,name,birthday,sex,telephone,email,status,code) VALUES (?,?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,user.getUid(),user.getUsername(),user.getPassword(),user.getName(),user.getBirthday(),user.getSex(),user.getTelephone(),user.getEmail(),user.getStatus(),user.getCode());
    }

    /*
    *
    * 用户激活
    * */
    public int active(String code) {
        return jdbcTemplate.update("UPDATE tab_user SET STATUS = 'Y' where CODE = ?",code);
    }

    /*
    *
    * 用户登录
    *
    * */
    public User login(String username, String password) {
        User user = null;
        try {
            user = jdbcTemplate.queryForObject("select * from tab_user where username = ? AND PASSWORD = ?",new BeanPropertyRowMapper<>(User.class),username,password);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("找不到用户User[username="+username+",password="+password+"]");
        }
        return user;
    }

    /*
    *
    * 判断用户是否存在，存在不让注册
    *
    * */
    public User findByUsername(String username) {
        User user = null;
        try {
            user = jdbcTemplate.queryForObject("select * from tab_user where username = ?", new BeanPropertyRowMapper<>(User.class),username);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("找不到用户User[username="+username+"]");
        }
        return user;
    }
}
