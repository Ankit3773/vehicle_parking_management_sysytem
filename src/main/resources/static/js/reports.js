document.addEventListener('DOMContentLoaded', async () => {
    await ParkingApp.initProtectedPage('reports', 'Daily Operations and Revenue Report');

    const form = document.getElementById('report-form');
    const reportDateInput = document.getElementById('reportDate');
    const exportButton = document.getElementById('export-report');
    const today = new Date().toISOString().split('T')[0];
    reportDateInput.value = today;
    reportDateInput.max = today;

    const loadReport = async (date) => {
        ParkingApp.clearMessage('reports-message');

        try {
            const [dailyReport, revenueReport] = await Promise.all([
                Api.get(`/api/reports/daily?date=${date}`),
                Api.get(`/api/reports/revenue?date=${date}`)
            ]);

            document.getElementById('report-total-entries').textContent = dailyReport.totalEntries;
            document.getElementById('report-completed-exits').textContent = dailyReport.completedExits;
            document.getElementById('report-total-payments').textContent = revenueReport.totalPayments;
            document.getElementById('report-total-revenue').textContent = ParkingApp.formatCurrency(revenueReport.totalRevenue);

            const tableBody = document.getElementById('report-table-body');
            if (!dailyReport.records.length) {
                tableBody.innerHTML = ParkingApp.renderEmptyRow(7, 'No records available for the selected date.');
                return;
            }

            tableBody.innerHTML = dailyReport.records.map(record => `
                <tr>
                    <td><span class="mono-text">${record.vehicleNumber}</span></td>
                    <td>${record.ownerName}</td>
                    <td><span class="mono-text">${record.slotNumber}</span></td>
                    <td>${ParkingApp.formatDateTime(record.entryTime)}</td>
                    <td>${ParkingApp.formatDateTime(record.exitTime)}</td>
                    <td>${ParkingApp.statusBadge(record.status)}</td>
                    <td>${record.amount !== null ? ParkingApp.formatCurrency(record.amount) : '-'}</td>
                </tr>
            `).join('');
        } catch (error) {
            ParkingApp.showMessage('reports-message', 'error', error.message || 'Unable to load report data.');
            document.getElementById('report-table-body').innerHTML = ParkingApp.renderEmptyRow(7, 'Report data could not be loaded.');
        }
    };

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!reportDateInput.value) {
            ParkingApp.showMessage('reports-message', 'error', 'Please select a valid report date.');
            return;
        }
        await loadReport(reportDateInput.value);
    });

    exportButton.addEventListener('click', () => {
        alert('This button is kept as a demo placeholder for future PDF or Excel export.');
    });

    await loadReport(today);
});
