package com.company.controller;

import com.company.model.Customer;
import com.company.service.CustomerService;
import com.company.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 客户管理API控制器
 */
public class CustomerServlet extends HttpServlet {
    private CustomerService customerService = new CustomerService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 获取所有客户或搜索
                String keyword = request.getParameter("keyword");
                String level = request.getParameter("level");
                List<Customer> customers;

                if (keyword != null && !keyword.isEmpty()) {
                    customers = customerService.searchCustomers(keyword);
                } else if (level != null && !level.isEmpty()) {
                    customers = customerService.getCustomersByLevel(level);
                } else {
                    customers = customerService.getAllCustomers();
                }
                out.print(JsonUtil.success(customers));
            } else {
                // 获取单个客户
                int id = Integer.parseInt(pathInfo.substring(1));
                Customer customer = customerService.getCustomerById(id);
                if (customer != null) {
                    out.print(JsonUtil.success(customer));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(JsonUtil.error("客户不存在"));
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
            Customer customer = new Customer();
            customer.setCustomerName(JsonUtil.getStringValue(json, "customerName"));
            customer.setContactName(JsonUtil.getStringValue(json, "contactName"));
            customer.setPhone(JsonUtil.getStringValue(json, "phone"));
            customer.setAddress(JsonUtil.getStringValue(json, "address"));
            customer.setEmail(JsonUtil.getStringValue(json, "email"));
            customer.setCustomerLevel(JsonUtil.getStringValue(json, "customerLevel"));

            if (customer.getCustomerName() == null || customer.getCustomerName().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("客户名称不能为空"));
                return;
            }

            if (customerService.addCustomer(customer)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("添加客户失败"));
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
            Customer customer = new Customer();
            customer.setCustomerId(JsonUtil.getIntValue(json, "customerId"));
            customer.setCustomerName(JsonUtil.getStringValue(json, "customerName"));
            customer.setContactName(JsonUtil.getStringValue(json, "contactName"));
            customer.setPhone(JsonUtil.getStringValue(json, "phone"));
            customer.setAddress(JsonUtil.getStringValue(json, "address"));
            customer.setEmail(JsonUtil.getStringValue(json, "email"));
            customer.setCustomerLevel(JsonUtil.getStringValue(json, "customerLevel"));

            if (customerService.updateCustomer(customer)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("更新客户失败"));
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
                out.print(JsonUtil.error("请指定客户ID"));
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));

            if (customerService.hasOrders(id)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("该客户有订单记录，无法删除"));
                return;
            }

            if (customerService.deleteCustomer(id)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("删除客户失败"));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(JsonUtil.error(e.getMessage()));
        }
    }
}
