# 🏠 Real Estate Property Management System

A web-based retail real estate platform that allows users to view properties, book appointments for site visits, and manage listings. Admins can manage users, properties, and appointments.

---

## Features

### User Module
- Signup/Login for users and admins
- User authentication with role distinction
- Session management

### Property Management
- Admin can **add, edit, delete, and view** properties
- Properties include description, price, type, location, and optional image URLs
- All properties visible in grid/table format with sorting and filtering

### Appointment and Site Visit Module
- Users can schedule appointments to visit properties
- Admin can view all scheduled appointments
- Appointment status: `PENDING`, `CONFIRMED`, `CANCELLED`

### Admin Dashboard
- Overview of all properties and appointments
- CRUD operations on all modules
- Centralized control for user and property management

---

## Tech Stack

| Layer         | Technology        |
|---------------|-------------------|
| Frontend      | Vaadin (Java-based UI Framework) |
| Backend       | Spring Boot (Java) |
| Database      | MySQL             |
| ORM           | JPA (Hibernate)   |
| Build Tool    | Maven             |
| Authentication| Spring Security   |


