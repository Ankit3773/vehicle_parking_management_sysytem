document.addEventListener('DOMContentLoaded', async () => {
    await ParkingApp.initProtectedPage('search', 'Vehicle Record Search');

    const form = document.getElementById('search-form');
    const searchInput = document.getElementById('searchVehicleNumber');
    const summaryTarget = document.getElementById('vehicle-summary');
    const tableBody = document.getElementById('search-table-body');

    const runSearch = async () => {
        ParkingApp.clearMessage('search-message');

        try {
            const vehicleNumber = searchInput.value.trim();
            if (!vehicleNumber) {
                throw new Error('Please enter a vehicle number to search.');
            }

            const response = await Api.get(`/api/parking/search?vehicleNumber=${encodeURIComponent(vehicleNumber)}`);

            summaryTarget.innerHTML = ParkingApp.renderDetailGrid([
                { label: 'Vehicle Number', value: response.vehicleNumber },
                { label: 'Owner Name', value: response.ownerName },
                { label: 'Vehicle Type', value: response.vehicleType },
                { label: 'Color', value: response.color }
            ]);

            tableBody.innerHTML = response.records.map(record => `
                <tr>
                    <td><span class="mono-text">${record.slotNumber}</span></td>
                    <td>${ParkingApp.formatDateTime(record.entryTime)}</td>
                    <td>${ParkingApp.formatDateTime(record.exitTime)}</td>
                    <td>${ParkingApp.statusBadge(record.status)}</td>
                    <td>${ParkingApp.formatDuration(record.durationMinutes)}</td>
                    <td>${record.amount !== null ? ParkingApp.formatCurrency(record.amount) : '-'}</td>
                </tr>
            `).join('');
        } catch (error) {
            ParkingApp.showMessage('search-message', 'error', error.message);
            summaryTarget.innerHTML = '<div class="empty-state">No vehicle details available.</div>';
            tableBody.innerHTML = ParkingApp.renderEmptyRow(6, 'No records found.');
        }
    };

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        await runSearch();
    });

    searchInput.value = 'MP09AB1234';
    searchInput.addEventListener('input', () => {
        searchInput.value = searchInput.value.toUpperCase();
    });
    await runSearch();
});
