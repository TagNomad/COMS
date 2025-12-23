package com.company.controller;

import com.company.model.Category;
import com.company.service.CategoryService;
import com.company.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 分类管理API控制器
 */
public class CategoryServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有分类
                List<Category> categories = categoryService.getAllCategories();
                out.print(JsonUtil.success(categories));
            } else if (pathInfo.equals("/top")) {
                // 获取顶级分类
                List<Category> categories = categoryService.getTopLevelCategories();
                out.print(JsonUtil.success(categories));
            } else if (pathInfo.startsWith("/sub/")) {
                // 获取子分类
                int parentId = Integer.parseInt(pathInfo.substring(5));
                List<Category> categories = categoryService.getSubCategories(parentId);
                out.print(JsonUtil.success(categories));
            } else {
                // 获取单个分类
                int id = Integer.parseInt(pathInfo.substring(1));
                Category category = categoryService.getCategoryById(id);
                if (category != null) {
                    out.print(JsonUtil.success(category));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(JsonUtil.error("分类不存在"));
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String json = JsonUtil.readJsonFromRequest(request);
            Category category = new Category();
            category.setCategoryName(JsonUtil.getStringValue(json, "categoryName"));
            category.setParentId(JsonUtil.getIntValue(json, "parentId"));

            if (category.getCategoryName() == null || category.getCategoryName().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("分类名称不能为空"));
                return;
            }

            if (categoryService.addCategory(category)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("添加分类失败"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String json = JsonUtil.readJsonFromRequest(request);
            Category category = new Category();
            category.setCategoryId(JsonUtil.getIntValue(json, "categoryId"));
            category.setCategoryName(JsonUtil.getStringValue(json, "categoryName"));
            category.setParentId(JsonUtil.getIntValue(json, "parentId"));

            if (categoryService.updateCategory(category)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("更新分类失败"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("请指定分类ID"));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));

            if (categoryService.hasProducts(id)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("该分类下有产品，无法删除"));
                return;
            }

            if (categoryService.deleteCategory(id)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("删除分类失败"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }
}
