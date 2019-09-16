package com.haozhikeji.service;

import com.haozhikeji.bean.Favorite;
import com.haozhikeji.bean.PageBean;
import com.haozhikeji.bean.Route;
import com.haozhikeji.bean.User;
import com.haozhikeji.dao.FavoriteDao;
import com.haozhikeji.util.JdbcUtils;
import com.haozhikeji.util.MyBeanUtils;
import com.haozhikeji.util.PageUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteService {

    private FavoriteDao favoriteDao = new FavoriteDao();

    /**
     * 如果找到了Favorite对象，说明已经收藏了，返回true
     * @param rid
     * @param user
     * @return  已收藏，返回true，未收藏，返回false
     */
    public Boolean isFavorite(String rid, User user) {
        Favorite favorite = favoriteDao.isFavorite(rid,user);
        return favorite!=null;
    }

    /**
     * 添加收藏的方法。需要使用事务，保证：向tab_favorite表里添加数据，缸盖tab_route表里count字段值+1  两步操作同时成功/同时失败
     * @param favorite
     * @return
     */
    public Boolean addFavorite(Favorite favorite) throws SQLException {

        //1.获取到一个连接对象
        DataSource dataSource = JdbcUtils.getDataSource();

        //2.使用连接池对象，创建一个JDBCTemplate
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        //3.使用Spring框架里的一个类，开启线程的标志---表示后边要使用事务了
        TransactionSynchronizationManager.initSynchronization();

        //4.使用Spring框架里的一个工具类，从dataSource中获取一个连接
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try {
            //5.开启事务
            connection.setAutoCommit(false);

            //6.调用dao，添加收藏数据
            favoriteDao.addFavorite(favorite,jdbcTemplate);

            //7.调用dao，更新收藏数据
            favoriteDao.updateFavoriteCount(favorite.getRoute().getRid(),jdbcTemplate);

            //8.如果没有异常，提交事务
            connection.commit();

            return true;
        } catch (Exception e) {
            //8.如果出现异常，回滚事务
            connection.rollback();
            e.printStackTrace();
        } finally {
            //9.把Connetion对象的autoCommit重置为默认值
            connection.setAutoCommit(true);
            //10.清理线程标志
            TransactionSynchronizationManager.clearSynchronization();
        }
        return false;
    }


    public PageBean myFavorite(User user, int pageNumber, int pageSize) {
        PageBean pageBean = new PageBean<>();

        /*当前页码 */
        pageBean.setPageNumber(pageNumber);

        /*每页多少条*/
        pageBean.setPageSize(pageSize);

        /*总共多少数据*/
        int totalCount = favoriteDao.findTotalCount(user);
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
        List<Map<String,Object>> list  =  favoriteDao.myFavorite(user,index,pageSize);

        //把list封装成List<Favorite>
        List<Favorite> data = convert(list,user);
        pageBean.setData(data);
        return pageBean;
    }

    //把list封装成List<Favorite>
    private List<Favorite> convert(List<Map<String, Object>> list,User user) {
        List<Favorite> arrayList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            //每个Map是查询结果的一行数据
            Favorite favorite = MyBeanUtils.populate(map, Favorite.class);
            Route route = MyBeanUtils.populate(map, Route.class);
            favorite.setRoute(route);
            favorite.setUser(user);

            arrayList.add(favorite);
        }
        return arrayList;
    }
}
