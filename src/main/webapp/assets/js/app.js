const API_BASE = '../api';

// Admin Component Loading
async function loadAdminComponents(pageTitle) {
    await loadComponents(pageTitle, 'nav-admin.html');
}

// User Component Loading
async function loadUserComponents(pageTitle) {
    await loadComponents(pageTitle, 'nav-user.html');
}

async function loadComponents(pageTitle, navFile) {
    try {
        // Load Sidebar
        const navRes = await fetch(`../common/${navFile}`);
        if (navRes.ok) {
            document.getElementById('nav-placeholder').innerHTML = await navRes.text();
            // Highlight active link
            const currentPath = window.location.pathname.split('/').pop();
            document.querySelectorAll('.nav-link').forEach(link => {
                if (link.getAttribute('href') === currentPath) {
                    link.classList.add('active');
                } else {
                    link.classList.remove('active');
                }
            });
        }

        // Load Header
        const headerRes = await fetch('../common/header.html');
        if (headerRes.ok) {
            document.getElementById('header-placeholder').innerHTML = await headerRes.text();
            // Set Title
            const titleEl = document.getElementById('pageTitle');
            if (titleEl) titleEl.textContent = pageTitle;
            // Init Search
            initGlobalSearch();
        }

    } catch (e) {
        console.error('Error loading components:', e);
    }
}

function initGlobalSearch() {
    const searchInput = document.getElementById('globalSearchInput');
    if (!searchInput) return;

    searchInput.addEventListener('input', (e) => {
        const term = e.target.value.toLowerCase();
        const rows = document.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(term) ? '' : 'none';
        });
    });
}

// Utilities
const formatCurrency = (amount) => {
    return new Intl.NumberFormat('zh-CN', { style: 'currency', currency: 'CNY' }).format(amount);
};

const formatDate = (dateStr) => {
    if (!dateStr) return '';
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
    const url = `${API_BASE}${endpoint}`;
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            // If HTTP error (404, 500, etc), throw specific error
            throw new Error(`HTTP Error ${response.status}: ${response.statusText} at ${url}`);
        }
        const text = await response.text();
        try {
            const result = JSON.parse(text);
            if (!result.success) {
                throw new Error(result.message || '操作失败');
            }
            return result.data;
        } catch (e) {
            // JSON parsing failed, likely HTML returned
            console.error('JSON Parse Error. Server returned:', text);
            throw new Error(`Invalid JSON response from ${url}. Server returned: ${text.substring(0, 100)}...`);
        }
    } catch (error) {
        alert(error.message);
        console.error("Fetch API Error:", error);
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
// Auth & Navigation
async function loadTopNav() {
    try {
        const navRes = await fetch('../common/top-nav.html');
        if (navRes.ok) {
            const div = document.createElement('div');
            div.innerHTML = await navRes.text();
            document.body.insertBefore(div, document.body.firstChild);

            // Add padding to body to prevent content hidden by fixed nav
            document.body.style.paddingTop = '60px'; // Top nav height

            updateAuthUI();
        }
    } catch (e) {
        console.error("Failed to load top nav", e);
    }
}

function updateAuthUI() {
    const userStr = localStorage.getItem('user');
    const authArea = document.getElementById('authArea');
    if (!authArea) return;

    if (userStr) {
        const user = JSON.parse(userStr);
        // Create user menu HTML
        authArea.innerHTML = `
            <div class="user-menu" style="display: flex; align-items: center; gap: 10px;">
                <span>👋 Hi, ${user.username}</span>
                <a href="#" onclick="logout(event)" class="nav-btn" style="background:#ef4444; color:white;">退出</a>
            </div>
        `;
    } else {
        authArea.innerHTML = `
            <a href="../common/login.html" class="nav-btn">登录</a>
        `;
    }
}

function checkLogin() {
    const userStr = localStorage.getItem('user');
    const currentPath = window.location.pathname;

    // Pages that require login
    const protectedPaths = ['/user/', '/admin/'];
    // Pages specifically for public access (login page itself)
    const publicPaths = ['/common/login.html', '/common/register.html'];

    // const isProtected = protectedPaths.some(p => currentPath.includes(p));
    // const isPublic = publicPaths.some(p => currentPath.includes(p));

    // Allow user home to be viewed?? Maybe restrict access to specific features?
    // Requirement says "separate accounts", so likely restrict access.

    if (!userStr && protectedPaths.some(p => currentPath.includes(p))) {
        // Not logged in, trying to access protected page
        // Redirect to login page
        window.location.href = '../common/login.html';
        return;
    }

    if (userStr && publicPaths.some(p => currentPath.endsWith(p))) {
        // Already logged in, trying to access login page
        const user = JSON.parse(userStr);
        if (user.role === 'admin') {
            window.location.href = '../admin/dashboard.html';
        } else {
            window.location.href = '../user/home.html';
        }
    }
}

async function logout(e) {
    if (e) e.preventDefault();

    try {
        // Call backend to invalidate session
        await fetch(API_BASE + '/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ action: 'logout' })
        });
    } catch (err) {
        console.warn('Backend logout failed', err);
    } finally {
        // Always clear local state
        localStorage.removeItem('user');
        window.location.href = '../common/login.html';
    }
}
