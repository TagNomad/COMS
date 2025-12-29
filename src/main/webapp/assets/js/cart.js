/**
 * Simple Shopping Cart Logic using LocalStorage
 */

const Cart = {
    key: 'coms_cart',

    getItems() {
        const stored = localStorage.getItem(this.key);
        return stored ? JSON.parse(stored) : [];
    },

    addItem(product, quantity = 1) {
        const items = this.getItems();
        const existingInfo = items.find(item => item.productId === product.productId);
        
        if (existingInfo) {
            existingInfo.quantity += quantity;
        } else {
            items.push({
                productId: product.productId,
                productName: product.productName,
                price: product.price,
                imageUrl: product.imageUrl,
                quantity: quantity
            });
        }
        
        this.saveItems(items);
        this.updateUI();
        this.showToast('Added to cart!');
    },

    removeItem(index) {
        const items = this.getItems();
        items.splice(index, 1);
        this.saveItems(items);
        this.updateUI();
        // Return updated items for page rendering if needed
        return items;
    },

    updateQuantity(index, delta) {
        const items = this.getItems();
        if (items[index]) {
            items[index].quantity += delta;
            if (items[index].quantity < 1) items[index].quantity = 1;
            this.saveItems(items);
            this.updateUI();
        }
        return items;
    },

    clear() {
        localStorage.removeItem(this.key);
        this.updateUI();
    },

    saveItems(items) {
        localStorage.setItem(this.key, JSON.stringify(items));
    },

    getCount() {
        const items = this.getItems();
        return items.reduce((sum, item) => sum + item.quantity, 0);
    },

    getTotal() {
        const items = this.getItems();
        return items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    },

    updateUI() {
        const count = this.getCount();
        const badges = document.querySelectorAll('.cart-counter');
        badges.forEach(badge => {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'flex' : 'none';
        });
    },

    showToast(message) {
        // Simple toast notification
        const toast = document.createElement('div');
        toast.style.cssText = `
            position: fixed;
            bottom: 24px;
            right: 24px;
            background: rgba(0, 0, 0, 0.8);
            color: white;
            padding: 12px 24px;
            border-radius: 8px;
            z-index: 1000;
            animation: fadeIn 0.3s;
            font-weight: 500;
        `;
        toast.textContent = message;
        document.body.appendChild(toast);
        
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = 'opacity 0.3s';
            setTimeout(() => toast.remove(), 300);
        }, 2000);
    }
};

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
    Cart.updateUI();
});
