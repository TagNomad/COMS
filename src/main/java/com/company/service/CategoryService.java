package com.company.service;

import com.company.dao.CategoryDao;
import com.company.model.Category;
import java.util.List;

/**
 * 分类业务逻辑类
 */
public class CategoryService {
    private CategoryDao categoryDao = new CategoryDao();

    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    public Category getCategoryById(int id) {
        return categoryDao.findById(id);
    }

    public List<Category> getTopLevelCategories() {
        return categoryDao.findTopLevel();
    }

    public List<Category> getSubCategories(int parentId) {
        return categoryDao.findByParentId(parentId);
    }

    public boolean addCategory(Category category) {
        return categoryDao.insert(category);
    }

    public boolean updateCategory(Category category) {
        return categoryDao.update(category);
    }

    public boolean deleteCategory(int id) {
        if (categoryDao.hasProducts(id)) {
            return false;
        }
        return categoryDao.delete(id);
    }

    public boolean hasProducts(int id) {
        return categoryDao.hasProducts(id);
    }
}
