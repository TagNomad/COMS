package com.company.controller;

import com.company.model.Order;
import com.company.model.OrderItem;
import com.company.service.OrderService;
import com.company.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单管理API控制器
 */
public class OrderServlet extends HttpServlet {
    private OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有订单或按条件查询
                String status = request.getParameter("status");
                String customerId = request.getParameter("customerId");
                List<Order> orders;

                if (status != null && !status.isEmpty()) {
                    orders = orderService.getOrdersByStatus(status);
                } else if (customerId != null && !customerId.isEmpty()) {
                    orders = orderService.getOrdersByCustomerId(Integer.parseInt(customerId));
                } else {
                    orders = orderService.getAllOrders();
                }
                out.print(JsonUtil.success(orders));
            } else if (pathInfo.equals("/stats")) {
                // 获取销售统计
                String startDate = request.getParameter("startDate");
                String endDate = request.getParameter("endDate");
                if (startDate == null)
                    startDate = "2000-01-01";
                if (endDate == null)
                    endDate = "2099-12-31";

                BigDecimal totalSales = orderService.getTotalSales(startDate, endDate);
                int orderCount = orderService.getOrderCount(startDate, endDate);

                String statsJson = "{\"totalSales\":" + totalSales + ",\"orderCount\":" + orderCount + "}";
                out.print("{\"success\":true,\"data\":" + statsJson + "}");
            } else {
                // 获取单个订单详情
                int id = Integer.parseInt(pathInfo.substring(1));
                Order order = orderService.getOrderById(id);
                if (order != null) {
                    out.print(JsonUtil.success(order));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(JsonUtil.error("订单不存在"));
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
            Order order = new Order();
            order.setCustomerId(JsonUtil.getIntValue(json, "customerId"));
            order.setShippingAddress(JsonUtil.getStringValue(json, "shippingAddress"));

            // 解析订单明细
            List<OrderItem> items = parseOrderItems(json);
            order.setOrderItems(items);

            if (order.getCustomerId() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("请选择客户"));
                return;
            }

            if (items.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("请添加订单明细"));
                return;
            }

            if (orderService.createOrder(order)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("创建订单失败"));
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
            Integer orderId = JsonUtil.getIntValue(json, "orderId");
            String status = JsonUtil.getStringValue(json, "orderStatus");

            if (orderId == null || status == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("订单ID和状态不能为空"));
                return;
            }

            if (orderService.updateOrderStatus(orderId, status)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("更新订单状态失败"));
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
                out.print(JsonUtil.error("请指定订单ID"));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            if (orderService.deleteOrder(id)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("只能删除待审核状态的订单"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }

    /**
     * 解析订单明细JSON数组
     * 支持更灵活的格式
     */
    private List<OrderItem> parseOrderItems(String json) {
        List<OrderItem> items = new ArrayList<>();
        int itemsStartKey = json.indexOf("\"items\"");
        if (itemsStartKey == -1)
            return items;

        int arrayStart = json.indexOf("[", itemsStartKey);
        if (arrayStart == -1)
            return items;

        // 找到匹配的数组结束括号 ]
        int arrayEnd = -1;
        int bracketCount = 0;
        for (int i = arrayStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[')
                bracketCount++;
            else if (c == ']')
                bracketCount--;

            if (bracketCount == 0) {
                arrayEnd = i;
                break;
            }
        }

        if (arrayEnd == -1)
            return items;

        String itemsArrayJson = json.substring(arrayStart + 1, arrayEnd);

        // 简单的对象分割逻辑：查找 { 和 } 配对
        int objStart = 0;
        while (true) {
            int openBrace = itemsArrayJson.indexOf("{", objStart);
            if (openBrace == -1)
                break;

            // 找到匹配的 }
            int closeBrace = -1;
            int braceDepth = 0;
            for (int i = openBrace; i < itemsArrayJson.length(); i++) {
                char c = itemsArrayJson.charAt(i);
                if (c == '{')
                    braceDepth++;
                else if (c == '}')
                    braceDepth--;

                if (braceDepth == 0) {
                    closeBrace = i;
                    break;
                }
            }

            if (closeBrace == -1)
                break;

            String itemJson = itemsArrayJson.substring(openBrace, closeBrace + 1);

            // 使用JsonUtil解析单个对象
            Integer productId = JsonUtil.getIntValue(itemJson, "productId");
            Integer quantity = JsonUtil.getIntValue(itemJson, "quantity");

            if (productId != null && quantity != null) {
                OrderItem item = new OrderItem();
                item.setProductId(productId);
                item.setQuantity(quantity);
                items.add(item);
            }

            objStart = closeBrace + 1;
        }

        return items;
    }
}
