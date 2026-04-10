document.addEventListener('DOMContentLoaded', async () => {
    try {
        await ParkingApp.initProtectedPage('dashboard', 'Administrative Dashboard');

        const [summary, activeRecords] = await Promise.all([
            Api.get('/api/dashboard/summary'),
            Api.get('/api/parking/active')
        ]);

        document.getElementById('total-slots').textContent = summary.totalSlots;
        document.getElementById('occupied-slots').textContent = summary.occupiedSlots;
        document.getElementById('vacant-slots').textContent = summary.vacantSlots;
        document.getElementById('today-vehicle-count').textContent = summary.todayVehicleCount;
        document.getElementById('today-revenue').textContent = ParkingApp.formatCurrency(summary.todayRevenue);

        const occupancyRate = summary.totalSlots ? Math.round((summary.occupiedSlots / summary.totalSlots) * 100) : 0;
        document.getElementById('occupancy-rate').textContent = `${occupancyRate}%`;
        document.getElementById('occupancy-stat').textContent = `${occupancyRate}%`;

        const activeBody = document.getElementById('dashboard-active-body');
        if (!activeRecords.length) {
            activeBody.innerHTML = ParkingApp.renderEmptyRow(4, 'No active vehicles are parked right now.');
            return;
        }

        activeBody.innerHTML = activeRecords.map(record => `
            <tr>
                <td><span class="mono-text">${record.vehicleNumber}</span></td>
                <td>${record.slotNumber}</td>
                <td>${record.vehicleType}</td>
                <td>${ParkingApp.formatDateTime(record.entryTime)}</td>
            </tr>
        `).join('');
    } catch (error) {
        ParkingApp.showMessage('dashboard-message', 'error', error.message || 'Unable to load dashboard data.');
        document.getElementById('dashboard-active-body').innerHTML = ParkingApp.renderEmptyRow(4, 'Dashboard data could not be loaded.');
    }
});
