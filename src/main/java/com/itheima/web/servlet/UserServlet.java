package com.itheima.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.bean.ResultInfo;
import com.itheima.bean.User;
import com.itheima.service.UserService;
import com.itheima.util.MailUtils;
import com.itheima.util.Md5Utils;
import com.itheima.util.MyBeanUtils;
import com.itheima.util.UUIDUtils;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/user")
public class UserServlet extends BaseServlet{

    private UserService userService = new UserService();

    /*
    *
    *注册时，用户名是否可用的校验
    * 如果用户名已存在，是不可用的，要返回false
    * 如果用户名不存在，是可用的，要返回true
    *
    * */
    public void isUserValid(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.接收参数username
        String username = request.getParameter("username");

        //2.根据username去数据库里查询，用户名是否已经存在
        User user = userService.findByUsername(username);

        //3.根据用户名是否已经存在，返回结果
        response.getWriter().print(user == null);
    }


    /*
    *
    * 退出登录
    * */
    public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //销毁session对象
        request.getSession().invalidate();
        //跳转到登录页面(重定向)
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    /*
    *
    * 获得登录的状态
    * */
    public void getLoginInfo(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 从session里获取loginUser
            User user = (User) request.getSession().getAttribute("loginUser");
            //返回结果
            resultInfo = new ResultInfo(true,user);


        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }
    /*
    * 登录验证
    *
    * */
    public void login(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //校验验证码
            String check = request.getParameter("check");
            String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
            if (check.equalsIgnoreCase(checkcode_server)){//验证码正确，进行用户名和密码校验
                //1.接收请求参数
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                //用户名和密码校验
                User user =  userService.login(username,password);

                //返回结果
                if (user != null) {//用户名和密码正确
                    if (user.getStatus().equals("Y")){//用户已激活
                        resultInfo = new ResultInfo(true);

                        //把登录的User对象放到sessio里
                        request.getSession().setAttribute("loginUser",user);

                    }else {//用户未激活
                        resultInfo = new ResultInfo(false,null,"用户未激活");
                    }

                }else {
                    //用户名或者密码错误
                    resultInfo = new ResultInfo(false,null,"用户名或者密码错误");
                }


            }else {
                //验证码错误，返回结果
                resultInfo = new ResultInfo(false,null,"验证码错误");
            }

        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }

    }
    /*
    *
    * 用户激活
    *
    * */
    public void active(HttpServletRequest request,HttpServletResponse response) throws IOException {
        //1.接收请求参数
        String code = request.getParameter("code");
        //2.处理业务请求，激活账号
        userService.active(code);
        //3.跳转到登录页面
        response.sendRedirect(request.getContextPath()+"/login.html");
    }

    /*
    *
    * 用户注册
    *
    * */
    public void register(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            String check = request.getParameter("check");
            String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
            if (check.equalsIgnoreCase(checkcode_server)){//验证码正确，把表单提交的数据保存到数据库里
                //1.接收参数 : map
                Map<String, String[]> map = request.getParameterMap();
                //2.封装：User
                User user = MyBeanUtils.populate(map, User.class);
                user.setStatus("N");//默认是未激活状态
                user.setCode(UUIDUtils.getUuid());//设置一个激活码
                String md5 = Md5Utils.encodeByMd5(user.getPassword());
                user.setPassword(md5);//密码加密
                //3.处理业务请求。调用service，把User添加到数据库里
                Boolean success = userService.add(user);

                if (success){
                    //注册成功，给用户的邮箱发送激活邮件
                    String url = "http://localhost:8080/travel-49/user?action=active&code="+user.getCode();
                    String emailMsg="你的黑马旅游账号已经注册成功，请<a href='"+url+"'>点击激活</a>后再登录";
                    MailUtils.sendMail(user.getEmail(),emailMsg);
                }


                //4.返回结果
                resultInfo = new ResultInfo(true,success,"Ojbk..");
            }else {
                //验证码错误,返回结果
                resultInfo = new ResultInfo(false,null,"验证码错误");
            }

        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }
}
