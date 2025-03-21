# Social network

### Main business logic
- Registration
- Editing personal information
- Search for users (according to different fields (can separately) (username, email, firstname, lastname))
- Message exchange between users
- Public messages (tape/wall)
- Adding/removing the user to friends
- Viewing information about the user (profile)
- Communities

### Additionally:
- Comments on public messages
- Public messages by communities

## Installation & Setup

Before you start, make sure you have the following installed:
- **Java 17**
- **Docker**
- **PostgreSQL**
- **Maven**

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/m1nuano/social-network.git
cd social-network
```
### 2️⃣ Build the Project
```bash
mvn clean package
```
### 3️⃣ Create and Start Containers
```bash
docker-compose up -d --build
``` 
This will:
- Build and start the Spring Boot application
- Spin up a PostgreSQL database

Or just run `run.bat`

## Stop & Remove Containers
### To stop and remove containers
```bash
docker-compose down
```
### To clean up images and temporary data:
```bash
docker system prune -a
```
Or just run `delete.bat`

**WARNING**
If you have any inactive containers/images, then when executing this file they will be deleted


