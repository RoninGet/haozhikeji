package com.itheima.service;

import com.itheima.bean.*;
import com.itheima.dao.RouteDao;
import com.itheima.util.PageUtils;
import com.itheima.bean.Category;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteService {

    private RouteDao routeDao = new RouteDao();

    /*
    *
    * 黑马精选
    *
    * */
    public Map<String, List<Route>> usefulRoutes() {

        Map<String,List<Route>> map = new HashMap<>();

        //1.调用Dao查询数据库,得到人气旅游信息
        List<Route> hotRoute = routeDao.hotRoute();
        map.put("hots",hotRoute);

        //2.调用Dao查询数据库,得到最新旅游信息
        List<Route> newRoute = routeDao.newRoute();
        map.put("news",newRoute);

        //3.调用Dao查询数据库,得到主题旅游信息
        List<Route> themeRoute = routeDao.themeRoute();
        map.put("themes",themeRoute);

        return map;
    }

    public PageBean<Route> searchRoutes(String cid, String rname, int pageNumber, int pageSize) {

        PageBean<Route> pageBean = new PageBean<>();


        /*当前页码 */
        pageBean.setPageNumber(pageNumber);

        /*每页多少条*/
        pageBean.setPageSize(pageSize);

        /*总共多少数据*/
        int totalCount = routeDao.findTotalCount(cid,rname);
        pageBean.setTotalCount(totalCount);

        /*分了多少页*/
        int pageCount = PageUtils.calcPageCount(totalCount, pageSize);
        pageBean.setPageCount(pageCount);

        /*页码条从几开始显示*/
        int[] pagination = PageUtils.pagination(pageNumber, pageCount);
        pageBean.setStart(pagination[0]);

        /*页码条显示到几结束*/
        pageBean.setEnd(pagination[1]);

        /*当前页的数据集合*/
        int index = PageUtils.calcSqlLimitIndex(pageNumber,pageSize);
        List<Route> data = routeDao.searchRoutes(cid,rname,index,pageSize);
        pageBean.setData(data);
        return pageBean;

    }

    public Route findRuteById(String rid) {

        //1.查询路线详情
        Route route = routeDao.findRuteById(rid);

        //2.根据路线的cid字段值，查询所属分类信息
        Category category = routeDao.findCategoryById(route.getCid());
        route.setCategory(category);

        //3.根据路线的sid字段值，查询所属商家信息
        Seller seller = routeDao.findSellerById(route.getSid());
        route.setSeller(seller);

        //4.根据rid，查询路线的图片信息
        List<RouteImg> lisImgs = routeDao.findRouteImgByRid(rid);
        route.setRouteImgList(lisImgs);

        return route;
    }

    /**
     * 收藏排行榜
     * @param queryVO
     * @return
     */
    public PageBean rankRoutes(QueryVO queryVO) {
        PageBean pageBean = new PageBean<>();

        /*当前页码 */
        pageBean.setPageNumber(queryVO.getIntPageNumber());

        /*每页多少条*/
        pageBean.setPageSize(queryVO.getIntPageSize());

        /*总共多少数据*/
        int totalCount = routeDao.findTotalCount2(queryVO);
        pageBean.setTotalCount(totalCount);

        /*分了多少页*/
        int pageCount = PageUtils.calcPageCount(totalCount, queryVO.getIntPageSize());
        pageBean.setPageCount(pageCount);

        /*页码条从几开始显示*/
        int[] pagination = PageUtils.pagination(queryVO.getIntPageNumber(), pageCount);
        pageBean.setStart(pagination[0]);

        /*页码条显示到几结束*/
        pageBean.setEnd(pagination[1]);

        /*当前页的数据集合*/
        List<Route> data = routeDao.rankRoutes(queryVO);
        pageBean.setData(data);
        return pageBean;
    }
}
