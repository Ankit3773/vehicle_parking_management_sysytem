document.addEventListener('DOMContentLoaded', async () => {
    await ParkingApp.initProtectedPage('exit', 'Vehicle Exit and Billing');

    const form = document.getElementById('exit-form');
    const resultTarget = document.getElementById('exit-result');
    const activeVehiclesTarget = document.getElementById('active-vehicles');
    const vehicleNumberInput = document.getElementById('exitVehicleNumber');

    const renderActiveVehicles = async () => {
        const activeRecords = await Api.get('/api/parking/active');

        if (!activeRecords.length) {
            activeVehiclesTarget.innerHTML = '<div class="empty-state">No active vehicles are parked right now.</div>';
            return;
        }

        activeVehiclesTarget.innerHTML = `
            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>Vehicle</th>
                        <th>Slot</th>
                        <th>Type</th>
                        <th>Entry Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    ${activeRecords.map(record => `
                        <tr>
                            <td><span class="mono-text">${record.vehicleNumber}</span></td>
                            <td>${record.slotNumber}</td>
                            <td>${record.vehicleType}</td>
                            <td>${ParkingApp.formatDateTime(record.entryTime)}</td>
                        </tr>
                    `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    };

    try {
        await renderActiveVehicles();
    } catch (error) {
        ParkingApp.showMessage('exit-message', 'error', error.message || 'Unable to load active vehicle records.');
        activeVehiclesTarget.innerHTML = '<div class="empty-state">Active vehicle data could not be loaded.</div>';
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        ParkingApp.clearMessage('exit-message');

        try {
            const vehicleNumber = vehicleNumberInput.value.trim();
            if (!vehicleNumber) {
                throw new Error('Vehicle number is required to process exit.');
            }

            const response = await Api.post('/api/parking/exit', { vehicleNumber });
            const record = response.record;
            ParkingApp.showMessage('exit-message', 'success', response.message);
            resultTarget.innerHTML = ParkingApp.renderDetailGrid([
                { label: 'Vehicle Number', value: record.vehicleNumber },
                { label: 'Slot Number', value: record.slotNumber },
                { label: 'Entry Time', value: ParkingApp.formatDateTime(record.entryTime) },
                { label: 'Exit Time', value: ParkingApp.formatDateTime(record.exitTime) },
                { label: 'Parking Duration', value: ParkingApp.formatDuration(record.durationMinutes) },
                { label: 'Parking Fee', value: ParkingApp.formatCurrency(record.amount) }
            ]);
            form.reset();
            await renderActiveVehicles();
        } catch (error) {
            ParkingApp.showMessage('exit-message', 'error', error.message);
        }
    });

    vehicleNumberInput.addEventListener('input', () => {
        vehicleNumberInput.value = vehicleNumberInput.value.toUpperCase();
    });
});
