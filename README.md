# Социальная сеть
- Регистрация
- Редактирование личной информации
- Поиск пользователей
- Обмен сообщениями между пользователями
- Публичные сообщения(лента/стена)
- Добавление/Удаление пользователя в друзья
- Просмотр информации о пользователе(профиля)
- Сообщества

## Общие требования к системе
### Обязательные
- Соблюдение канонических принципов написания Java-программ и Java naming convention
- Соответствие архитектурному шаблону MVC
- Применение принципов сильной и слабой связности
- Продуманная модульная структура (логическая структура пакетов)
- Обоснованное использование шаблонов проектирования
- Наличие диаграммы базы данных
- Приведение БД к 3 нормальной форме
- Скрипт инициализации БД (SQL-скрипт или миграции через Liquibase/Flyway)
- Реализация ролевой модели пользователей (например, обычный пользователь и администратор)
- Unit-тесты на JUnit 5 и Mockito, покрывающие сервисный и контроллерный слои
- Обработка исключений и валидация входных данных (ControllerAdvice + Hibernate Validation)
- Многоуровневая система логирования (информация по каждому запросу и ошибки)
- Скрипты автоматической сборки и развертывания (bash-скрипт или Docker/Docker-compose)
- Документация по установке и развертыванию (файл `readme.md`)

### Дополнительные
- Соблюдение сроков (6 недель для Java Base, 2 недели для Java Intensive)
- Документирование API (Swagger/OpenAPI)
- Использование DTO и маппинг на Entity (MapStruct)
- Интеграционные тесты с реальными запросами (инициализация h2, Testcontainers)

# Технические требования
- JPA/Hibernate для работы с БД
- Maven/Gradle для сборки проекта
- PostgreSQL в качестве СУБД
- Spring/Spring Boot для конфигурирования и реализации IoC
- Spring Security для аутентификации пользователей  
