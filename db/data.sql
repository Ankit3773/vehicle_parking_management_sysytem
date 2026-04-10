USE vehicle_parking_db;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM payments;
DELETE FROM parking_records;
DELETE FROM vehicles;
DELETE FROM parking_slots;
DELETE FROM admins;
SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE admins AUTO_INCREMENT = 1;
ALTER TABLE vehicles AUTO_INCREMENT = 1;
ALTER TABLE parking_slots AUTO_INCREMENT = 1;
ALTER TABLE parking_records AUTO_INCREMENT = 1;
ALTER TABLE payments AUTO_INCREMENT = 1;

INSERT INTO admins (id, username, full_name, password_hash, active, created_at)
VALUES
    (1, 'admin', 'System Administrator', '$2a$10$xJTt5RX62IoNbyj/xrwVleFz.rX0Y2dfRfkkSUkbPCfiu7nWooC8i', b'1', NOW());

INSERT INTO parking_slots (id, slot_number, slot_type, occupied, active)
VALUES
    (1, 'C-01', 'CAR', b'0', b'1'),
    (2, 'C-02', 'CAR', b'1', b'1'),
    (3, 'C-03', 'CAR', b'0', b'1'),
    (4, 'C-04', 'CAR', b'0', b'1'),
    (5, 'C-05', 'CAR', b'0', b'1'),
    (6, 'C-06', 'CAR', b'0', b'1'),
    (7, 'B-01', 'BIKE', b'1', b'1'),
    (8, 'B-02', 'BIKE', b'0', b'1'),
    (9, 'B-03', 'BIKE', b'0', b'1'),
    (10, 'B-04', 'BIKE', b'0', b'1');

INSERT INTO vehicles (id, vehicle_number, owner_name, vehicle_type, color, created_at)
VALUES
    (1, 'MP09AB1234', 'Amit Sharma', 'CAR', 'White', NOW()),
    (2, 'MP04ZX9081', 'Riya Patel', 'CAR', 'Grey', NOW()),
    (3, 'CG07TT4500', 'Neha Tiwari', 'BIKE', 'Red', NOW()),
    (4, 'UP32PQ7788', 'Karan Singh', 'CAR', 'Black', NOW()),
    (5, 'DL01EE8899', 'Priya Nair', 'CAR', 'Silver', NOW()),
    (6, 'RJ14MN4501', 'Arun Mehta', 'BIKE', 'Blue', NOW()),
    (7, 'MH18QN5544', 'Sneha Kulkarni', 'CAR', 'Maroon', NOW()),
    (8, 'GJ01BK2201', 'Vikas Joshi', 'BIKE', 'Black', NOW());

INSERT INTO parking_records (id, vehicle_id, slot_id, entry_time, exit_time, status, duration_minutes, notes)
VALUES
    (1, 1, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), 'COMPLETED', 120, 'Morning demo record'),
    (2, 2, 2, DATE_SUB(NOW(), INTERVAL 55 MINUTE), NULL, 'ACTIVE', NULL, 'Currently parked car'),
    (3, 3, 7, DATE_SUB(NOW(), INTERVAL 20 MINUTE), NULL, 'ACTIVE', NULL, 'Currently parked bike'),
    (4, 4, 3, DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 6 HOUR), DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR), 'COMPLETED', 240, 'Previous day record'),
    (5, 5, 4, DATE_SUB(NOW(), INTERVAL 95 MINUTE), DATE_SUB(NOW(), INTERVAL 20 MINUTE), 'COMPLETED', 75, 'Afternoon visitor record'),
    (6, 6, 8, DATE_SUB(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 45 MINUTE), DATE_SUB(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 5 MINUTE), 'COMPLETED', 40, 'Archived bike record'),
    (7, 7, 5, DATE_SUB(NOW(), INTERVAL 170 MINUTE), DATE_SUB(NOW(), INTERVAL 45 MINUTE), 'COMPLETED', 125, 'Faculty visitor vehicle'),
    (8, 8, 9, DATE_SUB(NOW(), INTERVAL 110 MINUTE), DATE_SUB(NOW(), INTERVAL 60 MINUTE), 'COMPLETED', 50, 'Short duration bike record');

INSERT INTO payments (id, parking_record_id, amount, duration_minutes, payment_status, paid_at)
VALUES
    (1, 1, 30.00, 120, 'PAID', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
    (2, 4, 50.00, 240, 'PAID', DATE_SUB(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 2 HOUR)),
    (3, 5, 30.00, 75, 'PAID', DATE_SUB(NOW(), INTERVAL 20 MINUTE)),
    (4, 6, 20.00, 40, 'PAID', DATE_SUB(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 5 MINUTE)),
    (5, 7, 40.00, 125, 'PAID', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
    (6, 8, 20.00, 50, 'PAID', DATE_SUB(NOW(), INTERVAL 60 MINUTE));
