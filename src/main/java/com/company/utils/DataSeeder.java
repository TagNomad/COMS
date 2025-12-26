package com.company.utils;

import com.company.dao.CustomerDao;
import com.company.model.Customer;

/**
 * 模拟数据填充工具
 */
public class DataSeeder {
    public static void main(String[] args) {
        CustomerDao dao = new CustomerDao();
        
        Customer[] customers = {
            new Customer(null, "张伟", "张经理", "13800138001", "北京市朝阳区", "zhangwei@example.com", Customer.LEVEL_GOLD),
            new Customer(null, "李娜", "李小姐", "13912345678", "上海市浦东新区", "lina@example.com", Customer.LEVEL_SILVER),
            new Customer(null, "王芳", "王主任", "13688889999", "广州市天河区", "wangfang@example.com", Customer.LEVEL_NORMAL),
            new Customer(null, "刘洋", "刘先生", "13700001111", "深圳市南山区", "liuyang@example.com", Customer.LEVEL_DIAMOND),
            new Customer(null, "陈静", "陈老师", "13566667777", "杭州市西湖区", "chenjing@example.com", Customer.LEVEL_NORMAL),
            new Customer(null, "赵雷", "赵总", "13300133001", "成都市武侯区", "zhaolei@example.com", Customer.LEVEL_GOLD),
            new Customer(null, "孙俪", "孙女士", "18611112222", "南京市玄武区", "sunli@example.com", Customer.LEVEL_SILVER),
            new Customer(null, "周杰", "周先生", "15899998888", "武汉市江汉区", "zhoujie@example.com", Customer.LEVEL_NORMAL)
        };

        System.out.println("开始填充客户模拟数据...");
        int count = 0;
        for (Customer c : customers) {
            c.setPassword("123456");
            c.setStatus("激活");
            if (dao.insert(c)) {
                System.out.println("已添加客户: " + c.getCustomerName());
                count++;
            } else {
                System.err.println("添加失败: " + c.getCustomerName() + " (可能已存在)");
            }
        }
        System.out.println("填充完成，共添加 " + count + " 条数据。");
    }
}
