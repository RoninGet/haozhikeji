package com.haozhikeji.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haozhikeji.bean.PageBean;
import com.haozhikeji.bean.QueryVO;
import com.haozhikeji.bean.ResultInfo;
import com.haozhikeji.bean.Route;
import com.haozhikeji.service.RouteService;
import com.haozhikeji.util.MyBeanUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/route")
public class RouteServlet extends BaseServlet{

    private RouteService routeService = new RouteService();//alt+enter


    /**
     * 收藏排行榜
     * @param request
     * @param response
     * @throws IOException
     */
    public void rankRoutes(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.接收参数
            Map<String, String[]> map = request.getParameterMap();
            //2.封装实体
            QueryVO queryVO = MyBeanUtils.populate(map, QueryVO.class);
//            queryVO.setPageNumber(request.getParameter("pageNumber"));
            queryVO.setPageSize("8");
            //3.处理业务请求
            PageBean pageBean = routeService.rankRoutes(queryVO);
            //4.返回结果
            resultInfo = new ResultInfo(true,pageBean);
        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }





    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    public void findRuteById(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.接收参数
            String rid = request.getParameter("rid");

            Route route = routeService.findRuteById(rid);

            resultInfo = new ResultInfo(true,route);
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
    * 分页查询
    *
    * 根据cid和rname进行分页搜索
    *
    *
    * */
    public void searchRoutes(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.接收参数
            String cid = request.getParameter("cid");
            String rname = request.getParameter("rname");
            String pageNumberStr = request.getParameter("pageNumber");
            int pageNumber = 1;
            int pageSize = 8;
            if (pageNumberStr != null && !"".equals(pageNumberStr)){
                pageNumber = Integer.parseInt(pageNumberStr);
            }

            //2.调用service，根据条件进行分页搜索
           PageBean<Route> pageBean =  routeService.searchRoutes(cid,rname,pageNumber,pageSize);

            //3.返回結果
            resultInfo = new ResultInfo(true,pageBean);

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
    * 查询黑马精选数据
    *
    * */
    public void useful(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //1.调用service，查询得到黑马精选数据的Map
           Map<String,List<Route>> map = routeService.usefulRoutes();

           //2.返回结果
            resultInfo = new ResultInfo(true,map);

        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();

        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }
}
