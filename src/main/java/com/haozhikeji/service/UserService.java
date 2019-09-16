package com.haozhikeji.service;

import com.haozhikeji.bean.User;
import com.haozhikeji.dao.UserDao;
import com.haozhikeji.util.Md5Utils;

public class UserService {

    private UserDao userDao = new UserDao();


        /*
        *
        * 判断用户是否存在，存在不让注册
        *
        * */
    public User findByUsername(String username) {

        User user = userDao.findByUsername(username);
        return user;

    }


    /*
    *
    * 注册用户
    *
    * */
    public Boolean add(User user) {
        int count = userDao.add(user);
        return count == 1;

    }

    public boolean active(String code) {
        int count = userDao.active(code);
        return count == 1;
    }

    public User login(String username, String password) throws Exception {
        //密码加密
        password = Md5Utils.encodeByMd5(password);
        User user = userDao.login(username,password);
        return user;
    }


}
