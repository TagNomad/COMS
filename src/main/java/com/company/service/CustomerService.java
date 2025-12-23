package com.company.service;

import com.company.dao.CustomerDao;
import com.company.model.Customer;
import java.util.List;

/**
 * 客户业务逻辑类
 */
public class CustomerService {
    private CustomerDao customerDao = new CustomerDao();

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    public Customer getCustomerById(int id) {
        return customerDao.findById(id);
    }

    public List<Customer> searchCustomers(String keyword) {
        return customerDao.searchByName(keyword);
    }

    public List<Customer> getCustomersByLevel(String level) {
        return customerDao.findByLevel(level);
    }

    public boolean addCustomer(Customer customer) {
        return customerDao.insert(customer);
    }

    public boolean updateCustomer(Customer customer) {
        return customerDao.update(customer);
    }

    public boolean deleteCustomer(int id) {
        if (customerDao.hasOrders(id)) {
            return false;
        }
        return customerDao.delete(id);
    }

    public boolean hasOrders(int id) {
        return customerDao.hasOrders(id);
    }

    public boolean existsByUsername(String username) {
        return customerDao.existsByUsername(username);
    }

    public boolean existsByPhone(String phone) {
        return customerDao.existsByPhone(phone);
    }
}
