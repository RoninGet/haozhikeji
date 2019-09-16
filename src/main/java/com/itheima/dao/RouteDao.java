package com.itheima.dao;

import com.itheima.bean.*;
import com.itheima.util.JdbcUtils;
import com.itheima.util.PageUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RouteDao {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());


    /*
    * 人气旅游信息
    *
    * */
    public List<Route> hotRoute() {
        List<Route> query = jdbcTemplate.query("select * from tab_route where rflag=1 ORDER BY count DESC LIMIT 0,4", new BeanPropertyRowMapper<>(Route.class));
        return query;
    }

    /*
    *
    * 最新旅游信息
    *
    * */
    public List<Route> newRoute() {
        List<Route> query = jdbcTemplate.query("select * from tab_route where rflag=1 ORDER BY rdate DESC LIMIT 0,4", new BeanPropertyRowMapper<>(Route.class));
        return query;
    }

    /*
    *
    * 主题旅游信息
    *
    * */
    public List<Route> themeRoute() {
        List<Route> query = jdbcTemplate.query("select * from tab_route where isThemeTour=1 ORDER BY rdate DESC LIMIT 0,4", new BeanPropertyRowMapper<>(Route.class));
        return query;
    }

    /*
    *
    * 搜索框查询
    *
    * */
    public int findTotalCount(String cid, String rname) {
        String sql = "SELECT COUNT(*) FROM tab_route where rflag = 1";

        List<Object> params = new ArrayList<>();

        if (cid != null && !"".equals(cid)){
            sql+=" AND cid = ?";
            params.add(cid);
        }
        if (rname != null && !"".equals(rname)){
            sql+=" AND rname like ?";
            params.add("%" + rname + "%");
        }
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,params.toArray());
        return count;
    }

    public List<Route> searchRoutes(String cid, String rname, int index, int pageSize) {
        //动态拼接，重点掌握
        String sql = "select * from tab_route where rflag = 1";

        List<Object> params = new ArrayList<>();

        if (cid != null && !"".equals(cid)){
            sql+=" AND cid = ?";
            params.add(cid);
        }
        if (rname !=null && !"".equals(rname)){
            sql+=" AND rname like ?";
            params.add("%"+rname+"%");
        }
        sql+=" ORDER BY rdate DESC LIMIT ?,?";
        params.add(index);
        params.add(pageSize);


        List<Route> query = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Route.class), params.toArray());
        return query;

    }

    public Route findRuteById(String rid) {
        Route route = null;
        try {
            route = jdbcTemplate.queryForObject("select * from tab_route where rid = ?", new BeanPropertyRowMapper<>(Route.class), rid);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("找不到Route[rid="+rid+"]");
        }
        return route;
    }

    public Category findCategoryById(Integer cid) {
        Category category = null;
        try {
            category = jdbcTemplate.queryForObject("select * from tab_category where cid = ?", new BeanPropertyRowMapper<>(Category.class), cid);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("找不到Category[cid="+cid+"]");
        }
        return category;
    }

    public Seller findSellerById(Integer sid) {
        Seller seller = null;
        try {
            seller = jdbcTemplate.queryForObject("select * from tab_seller where sid = ?", new BeanPropertyRowMapper<>(Seller.class), sid);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("找不到Seller[sid="+sid+"]");
        }
        return seller;
    }


    public List<RouteImg> findRouteImgByRid(String rid) {
        List<RouteImg> imgs = jdbcTemplate.query("select * from tab_route_img where rid = ?", new BeanPropertyRowMapper<>(RouteImg.class), rid);
        return imgs;
    }

    /**
     * 收藏排行榜
     * @param queryVO
     * @return
     */
    public int findTotalCount2(QueryVO queryVO) {
        String sql = "select count(*) from tab_route where rflag = 1";
        ArrayList<Object> list = new ArrayList<>();

        //1.判断rname是否有值
        if (queryVO.getRname() != null && !"".equals(queryVO.getRname())){
            sql +=" and rname like ?";
            list.add("%"+queryVO.getRname()+"%");
        }

        //2.判断minprice是否有值
        if (queryVO.getMinprice() != null && !"".equals(queryVO.getMinprice())){
            sql += " and price>= ?";
            list.add(queryVO.getMinprice());
        }

        //3.判断maxprice是否有值
        if (queryVO.getMaxprice() != null && !"".equals(queryVO.getMaxprice())) {
            sql+=" and price<= ?";
            list.add(queryVO.getMaxprice());
        }

        return jdbcTemplate.queryForObject(sql,Integer.class,list.toArray());
    }

    public List<Route> rankRoutes(QueryVO queryVO) {
        String sql = "select * from tab_route where rflag = 1 ";
        PageBean pageBean = new PageBean<>();
        ArrayList<Object> list = new ArrayList<>();

        //1.判断rname是否有值
        if (queryVO.getRname() != null && !"".equals(queryVO.getRname())){
            sql+=" and rname like ?";
            list.add("%"+queryVO.getRname()+"%");
        }

        //2.判断minprice是否有值
        if (queryVO.getMinprice() !=null && !"".equals(queryVO.getMinprice())){
            sql+=" and price>= ?";
            list.add(queryVO.getMinprice());
        }

        //3.判断maxprice是否有值
        if (queryVO.getMaxprice() != null && !"".equals(queryVO.getMaxprice())){
            sql+=" and price <= ?";
            list.add(queryVO.getMaxprice());
        }
        sql+=" ORDER BY count DESC LIMIT ?,?";
        int index = PageUtils.calcSqlLimitIndex(queryVO.getIntPageNumber(),queryVO.getIntPageSize());
        list.add(index);
        list.add(queryVO.getIntPageSize());

        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Route.class),list.toArray());
    }
}
