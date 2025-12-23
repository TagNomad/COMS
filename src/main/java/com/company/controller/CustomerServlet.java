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
            customer.setContactName(JsonUtil.getStringValue(json, "contactName")); // Optional
            customer.setPhone(JsonUtil.getStringValue(json, "phone"));
            customer.setPassword(JsonUtil.getStringValue(json, "password")); // Added for registration
            customer.setStatus(JsonUtil.getStringValue(json, "status")); // Added for registration
            customer.setAddress(JsonUtil.getStringValue(json, "address")); // Optional
            customer.setEmail(JsonUtil.getStringValue(json, "email")); // Optional
            customer.setCustomerLevel(JsonUtil.getStringValue(json, "customerLevel")); // Optional

            if (customer.getCustomerName() == null || customer.getCustomerName().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("用户名不能为空"));
                return;
            }

            // 注册场景: 验证密码
            if (customer.getPassword() != null && customer.getPassword().length() < 6) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(JsonUtil.error("密码长度不能少于6位"));
                return;
            }

            // 检查用户名是否已被注册
            if (customerService.existsByUsername(customer.getCustomerName())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print(JsonUtil.error("该用户名已被注册"));
                return;
            }

            // 检查手机号是否已被注册
            if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
                if (customerService.existsByPhone(customer.getPhone())) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.print(JsonUtil.error("该手机号已被注册"));
                    return;
                }
            }

            // 设置默认值
            if (customer.getCustomerLevel() == null)
                customer.setCustomerLevel("普通");
            if (customer.getStatus() == null)
                customer.setStatus("active");

            if (customerService.addCustomer(customer)) {
                out.print(JsonUtil.success());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(JsonUtil.error("注册失败，请稍后重试"));
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
