# Vehicle Parking Management System

Web-based MCA academic project built with HTML, CSS, JavaScript, Spring Boot, and MySQL.

## Overview

This project demonstrates the daily workflow of a parking facility in a simple, viva-friendly structure:

- administrator login
- dashboard metrics
- vehicle entry and slot allocation
- vehicle exit and billing
- slot status monitoring
- vehicle record search
- daily report and revenue report

The codebase follows a clear `controller -> service -> repository -> model` architecture so it stays easy to explain during project review.

## Tech Stack

- Frontend: HTML, CSS, JavaScript
- Backend: Java 17, Spring Boot
- Database: MySQL

## Project Structure

```text
vehicle
├── db
│   ├── data.sql
│   └── schema.sql
├── docs
│   └── ACADEMIC_DEMO_GUIDE.md
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/mca/vehicleparking
    │   │   ├── config
    │   │   ├── controller
    │   │   ├── dto
    │   │   ├── exception
    │   │   ├── model
    │   │   ├── repository
    │   │   ├── service
    │   │   └── util
    │   └── resources
    │       ├── application.properties
    │       └── static
    └── test
        └── java/com/mca/vehicleparking
```

## Main Modules

- Admin Authentication
- Administrative Dashboard
- Vehicle Entry Management
- Parking Slot Status Overview
- Vehicle Exit and Billing
- Vehicle Record Search
- Daily Operations and Revenue Report

## Database Design

Tables used in the project:

- `admins`
- `vehicles`
- `parking_slots`
- `parking_records`
- `payments`

Relationships:

- one vehicle can have many parking records
- one parking slot can be used in many parking records over time
- one parking record can have one payment record

Business rules:

- only vacant active slots can be assigned
- one vehicle cannot have more than one active parking record
- exit updates the record, releases the slot, and creates a payment
- fee logic:
  - up to 1 hour: `₹20`
  - every additional started hour: `₹10`

## Backend APIs

Authentication:

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

Dashboard:

- `GET /api/dashboard/summary`

Parking:

- `POST /api/parking/entry`
- `POST /api/parking/exit`
- `GET /api/parking/active`
- `GET /api/parking/search?vehicleNumber=MP09AB1234`

Slots:

- `GET /api/slots`

Reports:

- `GET /api/reports/daily?date=2026-04-10`
- `GET /api/reports/revenue?date=2026-04-10`

## Setup

1. Create the database and tables:

```bash
mysql < /Users/ankitkumar/Downloads/vehicle/db/schema.sql
```

2. Load the seed/demo data:

```bash
mysql vehicle_parking_db < /Users/ankitkumar/Downloads/vehicle/db/data.sql
```

3. Start the application:

```bash
mvn spring-boot:run
```

4. Open the app:

```text
http://localhost:8080
```

## Demo Credentials

- Username: `admin`
- Password: `admin123`

## Application Configuration

Default database configuration in [application.properties](/Users/ankitkumar/Downloads/vehicle/src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_parking_db
spring.datasource.username=root
spring.datasource.password=
```

Optional environment overrides:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## Sample Request Bodies

Login:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Vehicle Entry:

```json
{
  "vehicleNumber": "MH12DE3434",
  "ownerName": "Rahul Deshmukh",
  "vehicleType": "CAR",
  "color": "Silver",
  "notes": "Academic demo vehicle"
}
```

Vehicle Exit:

```json
{
  "vehicleNumber": "MH12DE3434"
}
```

## Validation and Error Handling

- request DTOs use bean validation
- invalid requests return structured JSON error responses
- unauthorized API access returns `401`
- meaningful messages are returned for:
  - invalid login
  - duplicate active parking
  - no vacant slot available
  - vehicle not found
  - invalid report/search input

## Tests and Verification

Run automated tests:

```bash
mvn test
```

Recommended manual checks:

1. Login with valid credentials
2. Add a new vehicle entry and confirm slot assignment
3. Try duplicate entry for the same vehicle
4. Process exit and verify fee generation
5. Check slot status after exit
6. Search the vehicle number
7. Open daily report and revenue report

## Demo and Screenshot Guide

See [ACADEMIC_DEMO_GUIDE.md](/Users/ankitkumar/Downloads/vehicle/docs/ACADEMIC_DEMO_GUIDE.md) for:

- best demo flow
- recommended live demo sequence
- screenshot order
- sample vehicle data
- presentation notes

## Notes for Academic Submission

- The project intentionally avoids unnecessary complexity.
- The UI is designed for clean screenshots and report usage.
- Important backend service methods include comments for viva explanation.
- Authentication is session-based for simplicity and academic readability.
