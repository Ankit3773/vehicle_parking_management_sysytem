document.addEventListener('DOMContentLoaded', async () => {
    await ParkingApp.initProtectedPage('slots', 'Parking Slot Status Overview');

    const loadSlots = async () => {
        ParkingApp.clearMessage('slots-message');

        try {
            const slots = await Api.get('/api/slots');
            document.getElementById('slot-total').textContent = slots.length;
            document.getElementById('slot-occupied').textContent = slots.filter(slot => slot.occupied).length;
            document.getElementById('slot-vacant').textContent = slots.filter(slot => !slot.occupied).length;
            document.getElementById('slot-car').textContent = slots.filter(slot => slot.slotType === 'CAR').length;

            const body = document.getElementById('slot-table-body');
            if (!slots.length) {
                body.innerHTML = ParkingApp.renderEmptyRow(3, 'No slots found.');
                return;
            }

            body.innerHTML = slots.map(slot => `
                <tr>
                    <td><span class="mono-text">${slot.slotNumber}</span></td>
                    <td>${slot.slotType}</td>
                    <td>${ParkingApp.statusBadge(slot.occupied ? 'OCCUPIED' : 'VACANT')}</td>
                </tr>
            `).join('');
        } catch (error) {
            ParkingApp.showMessage('slots-message', 'error', error.message || 'Unable to load slot data.');
            document.getElementById('slot-table-body').innerHTML = ParkingApp.renderEmptyRow(3, 'Slot data could not be loaded.');
        }
    };

    document.getElementById('refresh-slots').addEventListener('click', loadSlots);
    await loadSlots();
});
