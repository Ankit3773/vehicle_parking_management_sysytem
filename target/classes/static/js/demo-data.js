window.DemoData = (() => {
    const STATE_KEY = 'vpms_demo_state';
    const SESSION_KEY = 'vpms_demo_session';

    const normalizeVehicleNumber = (vehicleNumber) =>
        (vehicleNumber || '').trim().toUpperCase().replace(/\s+/g, '');

    const sameDay = (value, dateString) => {
        if (!value) {
            return false;
        }
        return new Date(value).toISOString().slice(0, 10) === dateString;
    };

    const calculateFee = (durationMinutes) => {
        if (durationMinutes <= 60) {
            return 20;
        }
        return 20 + (Math.ceil((durationMinutes - 60) / 60) * 10);
    };

    const minutesBetween = (start, end) => {
        const duration = Math.ceil((new Date(end).getTime() - new Date(start).getTime()) / 60000);
        return Math.max(1, duration);
    };

    const sortRecords = (records) =>
        [...records].sort((first, second) => new Date(second.entryTime) - new Date(first.entryTime));

    const applySlotOccupancy = (slots, records) => {
        slots.forEach(slot => {
            slot.occupied = false;
        });

        records
            .filter(record => record.status === 'ACTIVE')
            .forEach(record => {
                const slot = slots.find(item => item.slotNumber === record.slotNumber);
                if (slot) {
                    slot.occupied = true;
                }
            });
    };

    const createSeedState = () => {
        const now = new Date();
        const minutesAgo = (minutes) => new Date(now.getTime() - minutes * 60000).toISOString();
        const hoursAgo = (hours) => new Date(now.getTime() - hours * 3600000).toISOString();
        const yesterday = new Date(now.getTime() - 24 * 3600000);
        const yesterdayAt = (hours, minutes) => {
            const value = new Date(yesterday);
            value.setHours(hours, minutes, 0, 0);
            return value.toISOString();
        };

        const recordOneEntry = hoursAgo(3);
        const recordOneExit = minutesAgo(35);
        const recordThreeEntry = hoursAgo(6);
        const recordThreeExit = hoursAgo(4);
        const recordFiveEntry = yesterdayAt(10, 0);
        const recordFiveExit = yesterdayAt(14, 20);

        const records = [
            {
                id: 1,
                vehicleNumber: 'MP09AB1234',
                ownerName: 'Amit Sharma',
                vehicleType: 'CAR',
                color: 'White',
                slotNumber: 'C-01',
                entryTime: recordOneEntry,
                exitTime: recordOneExit,
                status: 'COMPLETED',
                durationMinutes: minutesBetween(recordOneEntry, recordOneExit),
                amount: calculateFee(minutesBetween(recordOneEntry, recordOneExit))
            },
            {
                id: 2,
                vehicleNumber: 'MP04ZX9081',
                ownerName: 'Riya Patel',
                vehicleType: 'CAR',
                color: 'Grey',
                slotNumber: 'C-02',
                entryTime: minutesAgo(52),
                exitTime: null,
                status: 'ACTIVE',
                durationMinutes: null,
                amount: null
            },
            {
                id: 3,
                vehicleNumber: 'MP20LM2201',
                ownerName: 'Sandeep Verma',
                vehicleType: 'BIKE',
                color: 'Blue',
                slotNumber: 'B-02',
                entryTime: recordThreeEntry,
                exitTime: recordThreeExit,
                status: 'COMPLETED',
                durationMinutes: minutesBetween(recordThreeEntry, recordThreeExit),
                amount: calculateFee(minutesBetween(recordThreeEntry, recordThreeExit))
            },
            {
                id: 4,
                vehicleNumber: 'CG07TT4500',
                ownerName: 'Neha Tiwari',
                vehicleType: 'BIKE',
                color: 'Red',
                slotNumber: 'B-01',
                entryTime: minutesAgo(28),
                exitTime: null,
                status: 'ACTIVE',
                durationMinutes: null,
                amount: null
            },
            {
                id: 5,
                vehicleNumber: 'UP32PQ7788',
                ownerName: 'Karan Singh',
                vehicleType: 'CAR',
                color: 'Black',
                slotNumber: 'C-03',
                entryTime: recordFiveEntry,
                exitTime: recordFiveExit,
                status: 'COMPLETED',
                durationMinutes: minutesBetween(recordFiveEntry, recordFiveExit),
                amount: calculateFee(minutesBetween(recordFiveEntry, recordFiveExit))
            }
        ];

        const slots = [
            { id: 1, slotNumber: 'C-01', slotType: 'CAR', occupied: false },
            { id: 2, slotNumber: 'C-02', slotType: 'CAR', occupied: false },
            { id: 3, slotNumber: 'C-03', slotType: 'CAR', occupied: false },
            { id: 4, slotNumber: 'C-04', slotType: 'CAR', occupied: false },
            { id: 5, slotNumber: 'C-05', slotType: 'CAR', occupied: false },
            { id: 6, slotNumber: 'C-06', slotType: 'CAR', occupied: false },
            { id: 7, slotNumber: 'B-01', slotType: 'BIKE', occupied: false },
            { id: 8, slotNumber: 'B-02', slotType: 'BIKE', occupied: false },
            { id: 9, slotNumber: 'B-03', slotType: 'BIKE', occupied: false },
            { id: 10, slotNumber: 'B-04', slotType: 'BIKE', occupied: false }
        ];

        applySlotOccupancy(slots, records);

        return {
            admin: {
                username: 'admin',
                password: 'admin123',
                fullName: 'System Administrator'
            },
            slots,
            records,
            lastRecordId: 5
        };
    };

    const getState = () => {
        const existing = localStorage.getItem(STATE_KEY);
        if (existing) {
            return JSON.parse(existing);
        }

        const seedState = createSeedState();
        localStorage.setItem(STATE_KEY, JSON.stringify(seedState));
        return seedState;
    };

    const saveState = (state) => {
        localStorage.setItem(STATE_KEY, JSON.stringify(state));
    };

    const getCurrentAdmin = () => {
        const session = sessionStorage.getItem(SESSION_KEY);
        return session ? JSON.parse(session) : null;
    };

    return {
        initialize() {
            getState();
        },

        isAuthenticated() {
            return Boolean(getCurrentAdmin());
        },

        login(username, password) {
            const state = getState();
            if (username.trim().toLowerCase() !== state.admin.username || password !== state.admin.password) {
                throw new Error('Invalid username or password.');
            }

            const session = {
                username: state.admin.username,
                fullName: state.admin.fullName
            };

            sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
            return session;
        },

        logout() {
            sessionStorage.removeItem(SESSION_KEY);
        },

        getCurrentAdmin,

        reset() {
            const session = getCurrentAdmin();
            localStorage.setItem(STATE_KEY, JSON.stringify(createSeedState()));
            if (session) {
                sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
            }
        },

        getSummary() {
            const state = getState();
            const today = new Date().toISOString().slice(0, 10);

            return {
                totalSlots: state.slots.length,
                occupiedSlots: state.slots.filter(slot => slot.occupied).length,
                vacantSlots: state.slots.filter(slot => !slot.occupied).length,
                todayVehicleCount: state.records.filter(record => sameDay(record.entryTime, today)).length,
                todayRevenue: state.records
                    .filter(record => record.status === 'COMPLETED' && sameDay(record.exitTime, today))
                    .reduce((total, record) => total + (record.amount || 0), 0)
            };
        },

        getSlots() {
            const state = getState();
            return [...state.slots].sort((first, second) => first.slotNumber.localeCompare(second.slotNumber));
        },

        getActiveRecords() {
            const state = getState();
            return sortRecords(state.records.filter(record => record.status === 'ACTIVE'));
        },

        getFeaturedVehicleNumber() {
            const state = getState();
            return sortRecords(state.records)[0]?.vehicleNumber || '';
        },

        createEntry(payload) {
            const state = getState();
            const vehicleNumber = normalizeVehicleNumber(payload.vehicleNumber);

            if (state.records.some(record => record.vehicleNumber === vehicleNumber && record.status === 'ACTIVE')) {
                throw new Error(`Vehicle ${vehicleNumber} already has an active parking record.`);
            }

            const slot = state.slots.find(item => item.slotType === payload.vehicleType && !item.occupied);
            if (!slot) {
                throw new Error(`No vacant ${payload.vehicleType.toLowerCase()} slot is available right now.`);
            }

            slot.occupied = true;
            const record = {
                id: state.lastRecordId + 1,
                vehicleNumber,
                ownerName: payload.ownerName.trim(),
                vehicleType: payload.vehicleType,
                color: payload.color.trim(),
                slotNumber: slot.slotNumber,
                entryTime: new Date().toISOString(),
                exitTime: null,
                status: 'ACTIVE',
                durationMinutes: null,
                amount: null,
                notes: payload.notes?.trim() || ''
            };

            state.lastRecordId += 1;
            state.records.push(record);
            saveState(state);
            return record;
        },

        processExit(vehicleNumberInput) {
            const state = getState();
            const vehicleNumber = normalizeVehicleNumber(vehicleNumberInput);
            const record = state.records.find(item => item.vehicleNumber === vehicleNumber && item.status === 'ACTIVE');

            if (!record) {
                throw new Error(`No active record found for ${vehicleNumber}.`);
            }

            const exitTime = new Date().toISOString();
            record.exitTime = exitTime;
            record.durationMinutes = minutesBetween(record.entryTime, exitTime);
            record.amount = calculateFee(record.durationMinutes);
            record.status = 'COMPLETED';

            const slot = state.slots.find(item => item.slotNumber === record.slotNumber);
            if (slot) {
                slot.occupied = false;
            }

            saveState(state);
            return record;
        },

        searchRecords(vehicleNumberInput) {
            const state = getState();
            const vehicleNumber = normalizeVehicleNumber(vehicleNumberInput);
            const records = sortRecords(state.records.filter(record => record.vehicleNumber === vehicleNumber));

            if (!records.length) {
                throw new Error(`No records found for ${vehicleNumber}.`);
            }

            const latestRecord = records[0];
            return {
                vehicleNumber,
                ownerName: latestRecord.ownerName,
                vehicleType: latestRecord.vehicleType,
                color: latestRecord.color,
                records
            };
        },

        getDailyReport(dateString) {
            const state = getState();
            const reportDate = dateString || new Date().toISOString().slice(0, 10);
            const records = sortRecords(state.records.filter(record => sameDay(record.entryTime, reportDate)));
            const completedExits = state.records.filter(record => record.status === 'COMPLETED' && sameDay(record.exitTime, reportDate)).length;

            return {
                reportDate,
                totalEntries: records.length,
                completedExits,
                totalPayments: completedExits,
                totalRevenue: state.records
                    .filter(record => record.status === 'COMPLETED' && sameDay(record.exitTime, reportDate))
                    .reduce((total, record) => total + (record.amount || 0), 0),
                records
            };
        }
    };
})();
