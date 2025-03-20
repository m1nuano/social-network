@echo off

echo Stop and remove containers
docker-compose down

echo Cleaning ALL! images and temporary data
docker system prune -a

echo The operation is completed. Click any key to exit
pause
