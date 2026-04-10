document.addEventListener('DOMContentLoaded', async () => {
    await ParkingApp.initProtectedPage('entry', 'Vehicle Entry Management');

    const form = document.getElementById('entry-form');
    const resultTarget = document.getElementById('entry-result');
    const vehicleNumberInput = document.getElementById('vehicleNumber');

    const renderDefaultState = async () => {
        const slots = await Api.get('/api/slots');
        const availableCarSlots = slots.filter(slot => slot.slotType === 'CAR' && !slot.occupied).length;
        const availableBikeSlots = slots.filter(slot => slot.slotType === 'BIKE' && !slot.occupied).length;

        resultTarget.innerHTML = ParkingApp.renderDetailGrid([
            { label: 'Available Car Slots', value: availableCarSlots },
            { label: 'Available Bike Slots', value: availableBikeSlots },
            { label: 'Mode', value: 'Live backend data' },
            { label: 'Suggested Number', value: 'MP22AA9090' }
        ]);
    };

    try {
        await renderDefaultState();
    } catch (error) {
        ParkingApp.showMessage('entry-message', 'error', error.message || 'Unable to load slot availability.');
        resultTarget.innerHTML = '<div class="empty-state">Slot availability could not be loaded.</div>';
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        ParkingApp.clearMessage('entry-message');

        const payload = {
            vehicleNumber: vehicleNumberInput.value.trim(),
            ownerName: document.getElementById('ownerName').value.trim(),
            vehicleType: document.getElementById('vehicleType').value,
            color: document.getElementById('color').value.trim(),
            notes: document.getElementById('notes').value.trim()
        };

        try {
            if (!payload.vehicleNumber || !payload.ownerName || !payload.color) {
                throw new Error('Vehicle number, owner name, and color are required.');
            }

            const response = await Api.post('/api/parking/entry', payload);
            const record = response.record;
            ParkingApp.showMessage('entry-message', 'success', response.message);
            resultTarget.innerHTML = ParkingApp.renderDetailGrid([
                { label: 'Vehicle Number', value: record.vehicleNumber },
                { label: 'Owner Name', value: record.ownerName },
                { label: 'Vehicle Type', value: record.vehicleType },
                { label: 'Allocated Slot', value: record.slotNumber },
                { label: 'Entry Time', value: ParkingApp.formatDateTime(record.entryTime) },
                { label: 'Status', value: record.status }
            ]);
            form.reset();
            document.getElementById('vehicleType').value = 'CAR';
            await renderDefaultState();
        } catch (error) {
            ParkingApp.showMessage('entry-message', 'error', error.message);
        }
    });

    vehicleNumberInput.addEventListener('input', () => {
        vehicleNumberInput.value = vehicleNumberInput.value.toUpperCase();
    });
});
