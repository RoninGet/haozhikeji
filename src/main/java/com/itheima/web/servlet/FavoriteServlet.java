package com.itheima.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.bean.*;
import com.itheima.service.FavoriteService;
import com.itheima.service.RouteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/favorite")
public class FavoriteServlet extends BaseServlet {

    private FavoriteService favoriteService = new FavoriteService();
    private RouteService routeService = new RouteService();

    /**
     *我的收藏
     * @param request
     * @param response
     */
    public void myFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.从session里面获得登录的User对象
            User user = (User) request.getSession().getAttribute("loginUser");

            if (user != null){//已登录状态，查询我的收藏数据

                //2.接收参数
                String pageNumberStr = request.getParameter("pageNumber");

                int pageNumber= 1;
                int pageSize = 12;
                if (pageNumberStr != null && !"".equals(pageNumberStr)){
                    pageNumber = Integer.parseInt(pageNumberStr);
                }

                //3.处理业务请求，调用service，分页查询我的收藏数据
                PageBean pageBean = favoriteService.myFavorite(user,pageNumber,pageSize);

                //4.返回结果
                resultInfo = new ResultInfo(true,pageBean);
            }else {
                //未登录状态，返回状态值-1
                resultInfo = new ResultInfo(true,-1);
            }


        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }

    /**
     * 添加收藏功能
     * @param request
     * @param response
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.获得当前登录的对象
            User user = (User) request.getSession().getAttribute("loginUser");

            if (user != null){//已登录状态
                //2.接收参数
                String rid = request.getParameter("rid");

                //3.封装实体
                Favorite favorite = new Favorite();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                favorite.setDate(format.format(new Date()));
                favorite.setUser(user);
                Route route = new Route();
                route.setRid(Integer.parseInt(rid));
                favorite.setRoute(route);

                //4.处理业务逻辑，调用service，把Favorite保存到数据库，添加收藏
                Boolean success = favoriteService.addFavorite(favorite);

                if (success){
                    //如果添加收藏成功，查询最新的收藏数量
                    Route route1 = routeService.findRuteById(rid);
                    resultInfo = new ResultInfo(true,route1.getCount());

                }else {
                    //如果添加收藏失败，返回结果-1
                    resultInfo = new ResultInfo(true,-1);
                }

            }else {//未登录状态，返回结果-2
                resultInfo = new ResultInfo(true,-2);

            }
        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }
    /**
     * 判断路线是否被收藏
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.从session里面获得登录的User对象
            User user = (User) request.getSession().getAttribute("loginUser");

            if (user != null){//已登录
                //2.接收参数
                String rid = request.getParameter("rid");

                //3.处理业务请求，调用service，根据rid和user判断是否收藏了
                Boolean isFavorite = favoriteService.isFavorite(rid,user);

                //4.返回结果
                resultInfo = new ResultInfo(true,isFavorite);

            }else {//未登录,返回未收藏
                resultInfo = new ResultInfo(true,false);

            }
        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }
}
