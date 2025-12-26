package com.company.utils;

import com.company.dao.CategoryDao;
import com.company.dao.ProductDao;
import com.company.model.Category;
import com.company.model.Product;
import java.math.BigDecimal;

/**
 * 药品模拟数据填充工具
 */
public class MedicineSeeder {
    public static void main(String[] args) {
        CategoryDao categoryDao = new CategoryDao();
        ProductDao productDao = new ProductDao();

        // 1. 确保存在“医药保健”分类
        Category mainCat = findOrCreateCategory(categoryDao, "医药保健", null);
        if (mainCat == null) {
            System.err.println("无法创建主分类");
            return;
        }

        // 2. 确保存在“中西药品”子分类
        Category subCat = findOrCreateCategory(categoryDao, "中西药品", mainCat.getCategoryId());
        if (subCat == null) {
            System.err.println("无法创建子分类");
            return;
        }

        Integer catId = subCat.getCategoryId();

        // 3. 准备20个药品数据
        Object[][] drugData = {
            {"阿莫西林胶囊", "用于敏感菌所致的呼吸道感染、泌尿生殖道感染等", "盒", 15.50, 100},
            {"布洛芬缓释胶囊", "用于缓解轻至中度疼痛，如头痛、关节痛、牙痛等", "盒", 22.00, 200},
            {"连花清瘟胶囊", "清瘟解毒，宣肺泄热。用于治疗流行性感冒", "盒", 14.80, 150},
            {"板蓝根颗粒", "清热解毒，凉血利咽。用于肺胃热盛所致的咽喉肿痛", "包", 12.00, 300},
            {"999感冒灵颗粒", "解热镇痛。用于感冒引起的头痛，发热，鼻塞，流涕", "盒", 18.50, 250},
            {"蒙脱石散", "用于成人及儿童急、慢性腹泻", "盒", 25.00, 80},
            {"罗红霉素分散片", "适用于化脓性链球菌引起的咽炎及扁桃体炎", "盒", 32.00, 60},
            {"头孢克肟分散片", "适用于对头孢克肟敏感的链球菌属、肺炎球菌等", "盒", 45.00, 50},
            {"藿香正气水", "解表化湿，理气和中。用于外感风寒、内伤湿滞", "盒", 10.50, 500},
            {"健胃消食片", "健胃消食。用于脾胃虚弱所致食积", "盒", 16.00, 120},
            {"葡萄糖酸钙口服溶液", "用于预防和治疗钙缺乏症，如骨质疏松等", "盒", 28.50, 90},
            {"维生素C泡腾片", "增强机体抵抗力，用于预防和治疗坏血病", "瓶", 35.00, 200},
            {"云南白药气雾剂", "活血散瘀，消肿止痛。用于跌打损伤，瘀血肿痛", "盒", 48.00, 40},
            {"西瓜霜喷剂", "清热解毒，消肿止痛。用于口舌生疮，牙龈肿痛", "瓶", 13.50, 180},
            {"扶他林软膏", "用于缓解肌肉、软组织和关节的轻至中度疼痛", "支", 38.00, 70},
            {"莫匹罗星软膏", "局部外用抗生素，适用于革兰阳性球菌引起的皮肤感染", "支", 26.00, 110},
            {"左氧氟沙星滴眼液", "用于治疗细菌性结膜炎、角膜炎等眼部感染", "瓶", 19.80, 130},
            {"糠酸莫米松鼻喷雾剂", "用于治疗成人、青少年和3至11岁儿童的季节性鼻炎", "瓶", 65.00, 30},
            {"咽立爽口含滴丸", "疏风散热，消肿止痛，清喉利咽", "瓶", 21.00, 160},
            {"补中益气丸", "补中益气。用于体倦乏力，内脏下垂", "盒", 30.00, 45}
        };

        System.out.println("开始填充药品数据...");
        int count = 0;
        for (Object[] data : drugData) {
            Product p = new Product();
            p.setProductName((String) data[0]);
            p.setCategoryId(catId);
            p.setDescription((String) data[1]);
            p.setUnit((String) data[2]);
            p.setPrice(new BigDecimal(data[3].toString()));
            p.setStockQuantity((Integer) data[4]);
            p.setStatus("在售");
            
            if (productDao.insert(p)) {
                System.out.println("已添加药品: " + p.getProductName());
                count++;
            } else {
                System.err.println("添加失败: " + p.getProductName());
            }
        }
        System.out.println("填充完成，共添加 " + count + " 条药品数据。");
    }

    private static Category findOrCreateCategory(CategoryDao dao, String name, Integer parentId) {
        // 这里简化逻辑，假设 findAll 能获取所有分类
        for (Category c : dao.findAll()) {
            if (c.getCategoryName().equals(name)) {
                return c;
            }
        }
        Category newCat = new Category();
        newCat.setCategoryName(name);
        newCat.setParentId(parentId);
        if (dao.insert(newCat)) {
            // 再次查询以获取ID
            for (Category c : dao.findAll()) {
                if (c.getCategoryName().equals(name)) {
                    return c;
                }
            }
        }
        return null;
    }
}
