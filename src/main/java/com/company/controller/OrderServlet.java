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
     * 简单解析订单明细JSON数组
     */
    private List<OrderItem> parseOrderItems(String json) {
        List<OrderItem> items = new ArrayList<>();
        int itemsStart = json.indexOf("\"items\"");
        if (itemsStart == -1)
            return items;

        int arrayStart = json.indexOf("[", itemsStart);
        int arrayEnd = json.indexOf("]", arrayStart);
        if (arrayStart == -1 || arrayEnd == -1)
            return items;

        String itemsJson = json.substring(arrayStart + 1, arrayEnd);
        String[] itemStrings = itemsJson.split("\\},\\{");

        for (String itemStr : itemStrings) {
            itemStr = itemStr.replace("{", "").replace("}", "");
            OrderItem item = new OrderItem();

            // 解析productId
            int pidStart = itemStr.indexOf("\"productId\"");
            if (pidStart != -1) {
                int colonIdx = itemStr.indexOf(":", pidStart);
                int endIdx = itemStr.indexOf(",", colonIdx);
                if (endIdx == -1)
                    endIdx = itemStr.length();
                String pidStr = itemStr.substring(colonIdx + 1, endIdx).trim();
                item.setProductId(Integer.parseInt(pidStr));
            }

            // 解析quantity
            int qtyStart = itemStr.indexOf("\"quantity\"");
            if (qtyStart != -1) {
                int colonIdx = itemStr.indexOf(":", qtyStart);
                int endIdx = itemStr.indexOf(",", colonIdx);
                if (endIdx == -1)
                    endIdx = itemStr.length();
                String qtyStr = itemStr.substring(colonIdx + 1, endIdx).trim();
                item.setQuantity(Integer.parseInt(qtyStr));
            }

            if (item.getProductId() != null && item.getQuantity() != null) {
                items.add(item);
            }
        }

        return items;
    }
}
