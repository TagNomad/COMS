package com.company.controller;

import com.company.dao.AdminsDao;
import com.company.dao.CustomerDao;
import com.company.model.Admins;
import com.company.model.Customer;
import com.company.utils.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证控制器
 * 处理管理员和普通用户的登录、登出和会话检查
 */
public class LoginServlet extends HttpServlet {

    private AdminsDao adminsDao = new AdminsDao();
    private CustomerDao customerDao = new CustomerDao();

    @Override
    public void init() throws ServletException {
        super.init();
        // 确保默认管理员账户存在且密码正确
        adminsDao.resetDefaultAdmin();
    }

    /**
     * POST: 处理登录请求
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String json = JsonUtil.readJsonFromRequest(request);
            String username = trimToNull(JsonUtil.getStringValue(json, "username"));
            String password = trimToNull(JsonUtil.getStringValue(json, "password"));
            String role = trimToNull(JsonUtil.getStringValue(json, "role"));
            String action = trimToNull(JsonUtil.getStringValue(json, "action")); // 支持 logout

            // 处理登出
            if ("logout".equals(action)) {
                handleLogout(request, response, out);
                return;
            }

            // 验证必填参数
            if (username == null || password == null || role == null) {
                sendError(response, out, HttpServletResponse.SC_BAD_REQUEST, "用户名、密码和角色不能为空");
                return;
            }

            // 根据角色分发处理
            Map<String, Object> sessionData;
            if ("admin".equals(role)) {
                sessionData = authenticateAdmin(username, password);
            } else if ("user".equals(role)) {
                sessionData = authenticateUser(username, password);
            } else {
                sendError(response, out, HttpServletResponse.SC_BAD_REQUEST, "无效的角色类型");
                return;
            }

            // 验证结果处理
            if (sessionData != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", sessionData);
                out.print(JsonUtil.success(sessionData));
            } else {
                sendError(response, out, HttpServletResponse.SC_UNAUTHORIZED, "用户名或密码错误");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, out, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "登录失败: " + e.getMessage());
        }
    }

    /**
     * GET: 检查登录状态
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            out.print(JsonUtil.success(session.getAttribute("user")));
        } else {
            out.print(JsonUtil.error("未登录"));
        }
    }

    /**
     * DELETE: 登出
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        handleLogout(request, response, out);
    }

    // ==================== 私有方法 ====================

    /**
     * 管理员认证
     */
    private Map<String, Object> authenticateAdmin(String username, String password) {
        Admins admin = adminsDao.findByUsername(username);
        if (admin != null && password.equals(admin.getPassword())) {
            adminsDao.updateLastLogin(admin.getAdminId());

            Map<String, Object> data = new HashMap<>();
            data.put("id", admin.getAdminId());
            data.put("username", admin.getUsername());
            data.put("role", "admin");
            data.put("level", admin.getRole()); // super_admin / admin
            return data;
        }
        return null;
    }

    /**
     * 用户认证
     */
    private Map<String, Object> authenticateUser(String username, String password) {
        Customer customer = customerDao.findByUsername(username);
        if (customer != null && password.equals(customer.getPassword())) {
            // 检查账户状态
            if (!"active".equals(customer.getStatus())) {
                return null; // 账户已禁用
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", customer.getCustomerId());
            data.put("username", customer.getCustomerName());
            data.put("role", "user");
            data.put("level", customer.getCustomerLevel());
            return data;
        }
        return null;
    }

    /**
     * 处理登出
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        out.print(JsonUtil.success("已登出"));
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, PrintWriter out, int status, String message) {
        response.setStatus(status);
        out.print(JsonUtil.error(message));
    }

    /**
     * 去除空白并返回 null (如果为空)
     */
    private String trimToNull(String str) {
        if (str == null)
            return null;
        str = str.trim();
        return str.isEmpty() ? null : str;
    }
}
