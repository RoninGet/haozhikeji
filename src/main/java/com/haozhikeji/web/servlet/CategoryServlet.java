package com.haozhikeji.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haozhikeji.bean.ResultInfo;
import com.haozhikeji.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/category")
public class CategoryServlet extends BaseServlet {

    private CategoryService categoryService = new CategoryService();

    public void queryAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = null;
        ResultInfo resultInfo = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            //1.调用service，得到所有的分类信息
            String category = categoryService.queryAll();

            //2.返回结果
            resultInfo = new ResultInfo(true,category,"");
        } catch (Exception e) {
            resultInfo = new ResultInfo(false,null,"服务器繁忙，请稍后再试.");
            e.printStackTrace();
        } finally {
            data = objectMapper.writeValueAsString(resultInfo);
            response.getWriter().print(data);
        }
    }

}
