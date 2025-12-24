const API_BASE = '/COMS/api';

// Utilities
const formatCurrency = (amount) => {
    return new Intl.NumberFormat('zh-CN', { style: 'currency', currency: 'CNY' }).format(amount);
};

const formatDate = (dateStr) => {
    if(!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN');
};

const showModal = (modalId) => {
    document.getElementById(modalId).classList.add('active');
};

const hideModal = (modalId) => {
    document.getElementById(modalId).classList.remove('active');
};

// API Calls
async function fetchAPI(endpoint, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        const result = await response.json();
        if (!result.success) {
            throw new Error(result.message || '操作失败');
        }
        return result.data;
    } catch (error) {
        alert(error.message);
        throw error;
    }
}

// Stats Loading
async function loadDashboardStats() {
    try {
        const stats = await fetchAPI('/order/stats');
        document.getElementById('totalSales').textContent = formatCurrency(stats.totalSales || 0);
        document.getElementById('orderCount').textContent = stats.orderCount || 0;
        
        // Load top selling products
        const topProducts = await fetchAPI('/product/top?limit=5');
        const tbody = document.getElementById('topProductsList');
        tbody.innerHTML = topProducts.map(p => `
            <tr>
                <td>${p.productName}</td>
                <td>${p.categoryName || '-'}</td>
                <td>${formatCurrency(p.price)}</td>
                <td>${p.stockQuantity}</td>
            </tr>
        `).join('');
    } catch (e) {
        console.error('Failed to load stats', e);
    }
}
