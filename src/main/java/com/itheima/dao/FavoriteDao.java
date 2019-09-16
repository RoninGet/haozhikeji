package com.itheima.dao;

import com.itheima.bean.Favorite;
import com.itheima.bean.Route;
import com.itheima.bean.User;
import com.itheima.util.JdbcUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class FavoriteDao {

    JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());

    public Favorite isFavorite(String rid, User user) {
        Favorite favorite = null;
        try {
            favorite = jdbcTemplate.queryForObject("select * from tab_favorite where rid = ? and uid = ?", new BeanPropertyRowMapper<>(Favorite.class), rid, user.getUid());
        } catch (EmptyResultDataAccessException e) {
            System.out.println("找不到收藏信息Favorite[rid="+rid+",uid="+user.getUid()+"]");
        }
        return favorite;


    }

    public void addFavorite(Favorite favorite, JdbcTemplate template) {
        template.update("INSERT INTO tab_favorite (rid, date, uid) VALUES (?,?,?)", favorite.getRoute().getRid(),favorite.getDate(),favorite.getUser().getUid());
    }

    public void updateFavoriteCount(Integer rid, JdbcTemplate template) {
        template.update("UPDATE tab_route SET COUNT = count+1 WHERE rid = ?",rid);
    }


    /**
     * 我的收藏
     * @param user
     * @return
     */
    public int findTotalCount(User user) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from tab_favorite where uid = ?", Integer.class, user.getUid());
        return count;
    }

    /**
     * 我的收藏
     * @param user
     * @param index
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> myFavorite(User user, int index, int pageSize) {
        /*List<Route> query = jdbcTemplate.query("select * from tab_route r , tab_favorite f where r.rid = f.rid and uid = ? limit ?,?", new BeanPropertyRowMapper<>(Route.class), user.getUid(), index, pageSize);
        return query;*/
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList("select * from tab_favorite f , tab_route r where r.rid = f.rid and uid = ? limit ?,?", user.getUid(), index, pageSize);
        return mapList;
    }
}
