package com.company.controller;

import com.company.model.Product;
import com.company.service.ProductService;
import com.company.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品管理API控制器
 */
public class ProductServlet extends HttpServlet {
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有产品或搜索
                String keyword = request.getParameter("keyword");
                String categoryId = request.getParameter("categoryId");
                List<Product> products;

                if (keyword != null && !keyword.isEmpty()) {
                    products = productService.searchProducts(keyword);
                } else if (categoryId != null && !categoryId.isEmpty()) {
                    products = productService.getProductsByCategory(Integer.parseInt(categoryId));
                } else {
                    products = productService.getAllProducts();
                }
                out.print(JsonUtil.success(products));
            } else if (pathInfo.equals("/top")) {
                // 获取热销产品
                String limitStr = request.getParameter("limit");
                int limit = limitStr != null ? Integer.parseInt(limitStr) : 10;
                List<Product> products = productService.getTopSellingProducts(limit);
                out.print(JsonUtil.success(products));
            } else {
                // 获取单个产品
                int id = Integer.parseInt(pathInfo.substring(1));
                Product product = productService.getProductById(id);
                if (product != null) {
                    out.print(JsonUtil.success(product));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(JsonUtil.error("产品不存在"));
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
            Product product = new Product();
            product.setProductName(JsonUtil.getStringValue(json, "productName"));
            product.setCategoryId(JsonUtil.getIntValue(json, "categoryId"));
            product.setDescription(JsonUtil.getStringValue(json, "description"));
            product.setUnit(JsonUtil.getStringValue(json, "unit"));
            product.setPrice(JsonUtil.getBigDecimalValue(json, "price"));
            product.setStockQuantity(JsonUtil.getIntValue(json, "stockQuantity"));
            product.setStatus(JsonUtil.getStringValue(json, "status"));
            product.setImageUrl(JsonUtil.getStringValue(json, "imageUrl"));

            if (product.getProductName() == null || product.getProductName().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("产品名称不能为空"));
                return;
            }

            if (productService.addProduct(product)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("添加产品失败"));
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
            Product product = new Product();
            product.setProductId(JsonUtil.getIntValue(json, "productId"));
            product.setProductName(JsonUtil.getStringValue(json, "productName"));
            product.setCategoryId(JsonUtil.getIntValue(json, "categoryId"));
            product.setDescription(JsonUtil.getStringValue(json, "description"));
            product.setUnit(JsonUtil.getStringValue(json, "unit"));
            product.setPrice(JsonUtil.getBigDecimalValue(json, "price"));
            product.setStockQuantity(JsonUtil.getIntValue(json, "stockQuantity"));
            product.setStatus(JsonUtil.getStringValue(json, "status"));
            product.setImageUrl(JsonUtil.getStringValue(json, "imageUrl"));

            if (productService.updateProduct(product)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("更新产品失败"));
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
                out.print(JsonUtil.error("请指定产品ID"));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            if (productService.deleteProduct(id)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("删除产品失败"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }
}
