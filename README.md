# 📔 My Diary Backend API

## Enterprise Secure Diary & Folder Management System

My Diary Backend is a secure RESTful API built using Spring Boot that enables users to safely create, organize, archive, restore, and manage encrypted personal diary entries.

The system follows enterprise backend development practices including layered architecture, JWT authentication, role-based authorization, encrypted data storage, and secure API communication.

This backend acts as the core service responsible for authentication validation, diary lifecycle management, folder organization, and protected data access.

---

## 🚀 Technology Stack

- Java
- Spring Boot
- Spring Security
- JWT Authentication
- MongoDB
- Maven
- REST API Architecture

---

## 🏗️ Backend Architecture

The project follows Clean Layered Architecture:

Controller Layer  
↓  
Service Layer  
↓  
Security Layer  
↓  
Repository Layer  
↓  
MongoDB Database  

### Architecture Benefits
- Separation of concerns
- Scalable system design
- Maintainable codebase
- Enterprise-ready structure

---

## 🔐 Security Implementation

### Authentication & Authorization
- JWT Access Token validation
- Secure authenticated endpoints
- Stateless session handling
- User-based resource access control

### Data Security
- Encrypted diary storage
- Secure password hashing
- Request authentication filtering
- Protected API routes

Only authenticated users can access diary and folder resources.

---

## 📔 Diary Management APIs

### Create Diary
POST `/diary/create`

Creates a new diary entry for authenticated user.

---

### Fetch Diary By ID
GET `/diary/fetch/{id}`

Fetch diary using status:
- ACTIVE
- TRASH
- ACHIEVED

---

### Update Diary
PUT `/diary/update/{id}`

Updates diary content.

---

### Move Diary To Trash
DELETE `/diary/delete/{id}`

Soft delete implementation.

---

### Permanent Delete
DELETE `/diary/permanent_delete/{id}`

Removes diary permanently from database.

---

### Archive Diary
DELETE `/diary/achieve/{id}`

Moves diary to archive state.

---

### Restore Diary
PUT `/diary/restore/{id}`

Restore diary from Trash or Archive.

---

### Fetch All Diaries
GET `/diary/fetch_all`

Returns user diary collection based on status.

---

### Search Diaries
GET `/diary/search?text=value`

Text-based diary search with pagination.

---

### Date-Based Diary APIs

#### Specific Date
GET `/diary/specific_date_diary?date=dd-MM-yyyy`

#### Today Diaries
GET `/diary/today_diary`

#### Weekly Diaries
GET `/diary/week_diary`

#### Monthly Diaries
GET `/diary/month_diary`

---

## 📁 Folder Management APIs

### Create Folder
POST `/folder/create`

Creates diary folder.

---

### Create Diary Inside Folder
POST `/folder/create/diary`

Adds diary into specific folder.

---

### Fetch Folder
GET `/folder/fetch/{id}`

Retrieve folder details.

---

### Update Folder
PUT `/folder/update/{id}`

Updates folder information.

---

### Delete Folder
DELETE `/folder/delete/{id}`

Soft delete folder.

---

### Archive Folder
DELETE `/folder/achieve/{id}`

Moves folder to archive.

---

### Restore Folder
PUT `/folder/restore/{id}`

Restores archived folder.

---

### Fetch All Folders
GET `/folder/fetch_all`

Returns all user folders.

---

### Search Folder
GET `/folder/search?text=value`

Folder name search with pagination.

---

### Fetch Diaries By Folder
GET `/folder/diary/{id}`

Returns diaries inside folder.

---

### Search Diaries Inside Folder
GET `/folder/search/diary/{id}?text=value`

Folder-specific diary search.

---

### Restore Diary To Folder
PUT `/folder/diary/restore/{id}`

Restores diary back into folder.

---

## 📊 Core Functional Capabilities

✅ Diary Lifecycle Management  
✅ Folder Organization System  
✅ Archive & Trash Workflow  
✅ Date-wise Diary Filtering  
✅ Pagination Support  
✅ Secure User Isolation  
✅ Search Optimization  

---

## 🛡️ Enterprise Security Practices

- Stateless authentication
- Secure endpoint authorization
- User ownership validation
- Soft delete recovery system
- Centralized exception handling

---

## 🚀 Deployment Ready

Supported Deployment Platforms:

- Railway
- Render
- AWS
- Docker Containers

Environment variables supported for production configuration.

---

## 🧪 Testing Scope

- Controller Testing
- Service Layer Testing
- API Endpoint Validation
- Authentication Testing

---

## 📈 Future Enhancements

- API Versioning
- Swagger Documentation
- Rate Limiting
- Activity Logging
- Microservices Migration

---

## 👨‍💻 Author

Ari Haran  
Full Stack Developer
