package com.company.service;

import com.company.dao.ProductDao;
import com.company.model.Product;
import java.util.List;

/**
 * 产品业务逻辑类
 */
public class ProductService {
    private ProductDao productDao = new ProductDao();

    public List<Product> getAllProducts() {
        return productDao.findAll();
    }

    public Product getProductById(int id) {
        return productDao.findById(id);
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return productDao.findByCategory(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        return productDao.searchByName(keyword);
    }

    public boolean addProduct(Product product) {
        return productDao.insert(product);
    }

    public boolean updateProduct(Product product) {
        return productDao.update(product);
    }

    public boolean deleteProduct(int id) {
        return productDao.delete(id);
    }

    public boolean updateStock(int productId, int quantity) {
        return productDao.updateStock(productId, quantity);
    }

    public List<Product> getTopSellingProducts(int limit) {
        return productDao.getTopSellingProducts(limit);
    }
}
