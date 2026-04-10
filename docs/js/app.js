window.ParkingApp = {
    async initProtectedPage(pageKey, pageTitle) {
        const admin = await this.ensureAuthenticated();
        this.renderSidebar(pageKey);
        this.renderTopbar(pageTitle, admin);
        this.attachTopbarActions();
        return admin;
    },

    async ensureAuthenticated() {
        try {
            return await Api.get('/api/auth/me');
        } catch (error) {
            window.location.href = '/login.html';
            throw error;
        }
    },

    renderSidebar(activePage) {
        const sidebar = document.getElementById('sidebar');
        if (!sidebar) {
            return;
        }

        const links = [
            { key: 'dashboard', label: 'Administrative Dashboard', href: '/dashboard.html' },
            { key: 'entry', label: 'Vehicle Entry Management', href: '/vehicle-entry.html' },
            { key: 'exit', label: 'Vehicle Exit and Billing', href: '/vehicle-exit.html' },
            { key: 'slots', label: 'Parking Slot Status Overview', href: '/slots.html' },
            { key: 'search', label: 'Vehicle Record Search', href: '/search.html' },
            { key: 'reports', label: 'Daily Operations and Revenue Report', href: '/reports.html' }
        ];

        sidebar.innerHTML = `
            <div class="brand-card">
                <p class="eyebrow">VPMS</p>
                <h2>Vehicle Parking Management</h2>
                <p class="muted-text">Academic demonstration module for administrator operations and reporting.</p>
            </div>
            <nav class="sidebar-links">
                ${links.map(link => `
                    <a class="sidebar-link ${link.key === activePage ? 'active' : ''}" href="${link.href}">
                        ${link.label}
                    </a>
                `).join('')}
            </nav>
        `;
    },

    renderTopbar(pageTitle, admin) {
        const topbar = document.getElementById('topbar');
        if (!topbar) {
            return;
        }

        topbar.innerHTML = `
            <div class="topbar-card">
                <div>
                    <h2>${pageTitle}</h2>
                    <p>Logged in as ${admin.fullName} (${admin.username}) for academic demonstration and reporting.</p>
                </div>
                <div class="toolbar-actions">
                    <button id="logout-button" class="ghost-btn" type="button">Logout</button>
                </div>
            </div>
        `;
    },

    attachTopbarActions() {
        const logoutButton = document.getElementById('logout-button');

        if (logoutButton) {
            logoutButton.addEventListener('click', async () => {
                await Api.post('/api/auth/logout', {});
                window.location.href = '/login.html';
            });
        }
    },

    showMessage(targetId, type, message) {
        const target = document.getElementById(targetId);
        if (!target) {
            return;
        }
        target.innerHTML = `<div class="message ${type}">${message}</div>`;
    },

    clearMessage(targetId) {
        const target = document.getElementById(targetId);
        if (target) {
            target.innerHTML = '';
        }
    },

    formatDateTime(value) {
        if (!value) {
            return '-';
        }
        return new Date(value).toLocaleString('en-IN', {
            year: 'numeric',
            month: 'short',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    formatCurrency(amount) {
        const value = Number(amount || 0);
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 2
        }).format(value);
    },

    formatDuration(minutes) {
        if (minutes === null || minutes === undefined) {
            return '-';
        }

        const hours = Math.floor(minutes / 60);
        const remainingMinutes = minutes % 60;

        if (hours === 0) {
            return `${minutes} minute(s)`;
        }

        return `${hours} hour(s) ${remainingMinutes} minute(s)`;
    },

    statusBadge(status) {
        const normalizedStatus = (status || '').toUpperCase();
        let badgeType = 'info';

        if (normalizedStatus === 'COMPLETED' || normalizedStatus === 'VACANT' || normalizedStatus === 'PAID') {
            badgeType = 'success';
        } else if (normalizedStatus === 'OCCUPIED') {
            badgeType = 'warning';
        }

        const label = normalizedStatus.charAt(0) + normalizedStatus.slice(1).toLowerCase();
        return `<span class="badge ${badgeType}">${label}</span>`;
    },

    renderDetailGrid(details) {
        return `
            <div class="detail-grid">
                ${details.map(item => `
                    <div class="detail-item">
                        <span>${item.label}</span>
                        <strong>${item.value}</strong>
                    </div>
                `).join('')}
            </div>
        `;
    },

    renderEmptyRow(colspan, message) {
        return `<tr><td colspan="${colspan}" class="empty-state">${message}</td></tr>`;
    }
};
