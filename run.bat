@echo off

echo Build the application
call mvnw clean package -DskipTests

echo Launch containers
call docker-compose up --build -d

echo Containers are launched. Click any key to exit
pause
