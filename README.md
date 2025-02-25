# Nexus ERP Backend  

Nexus ERP Backend is designed to handle and optimize all core operations. This service powers the logic, data management, and integrations that enable seamless workflows and interactions across the platform.  

## Features  
- **Project Management**: APIs for planning, tracking, and GitHub integration.  
- **Employee & Customer Management**: Manage users, roles, and relationships.  
- **Financial Management**: Support for multi-currency transactions and income gateway integrations (e.g., Stripe).
- **Budgeting**: Support for creating global budgets and project local budgets
- **Chat & Messaging**: Real-time communication with customers and employees.  
- **Event Scheduling**: APIs for custom schedules, reminders, and notifications.  
- **File Management**: Upload, organize, and manage files securely.  
- **Custom Notifications**: Event-driven notifications tailored for each user level.  

## Tech Stack  
- **Programming Language**: [Java](https://www.oracle.com/java/)  
- **Framework**: [Spring Boot](https://spring.io/projects/spring-boot)  
- **Database**: [PostgreSQL](https://www.postgresql.org/)
- **Authentication**: Spring Security with JWT
- **Real-Time Communication**: WebSocket for chat functionality  

## Installation  

### Prerequisites  
- Java 21 or higher  
- Maven
- PostgreSQL

### Steps  

1. Clone the repository:  
   ```bash  
   git clone https://github.com/your-repo/nexus-backend.git  
   cd nexus-backend  
   ```  

2. Import the project into your favorite IDE (e.g., IntelliJ IDEA, Eclipse).  

   3. Configure the application properties:  
      Create an `application.yml` file under the `src/main/resources` directory and define the required environment variables:  
      ```yaml  
      spring:  
        datasource:  
          url: jdbc:postgresql://<your-database-host>:5432/<your-database-name>  
          username: <your-database-username>  
          password: <your-database-password>
        mail:
          username: ${MAIL_USERNAME}
          password: ${MAIL_PASSWORD}
      stripe:
        secret-key: ${STRIPE_SECRET_KEY}
        webhook-secret: ${STRIPE_WEBHOOK_SECRET}
        client-id: ${STRIPE_CLIENT_ID}
        redirect-uri: ${STRIPE_REDIRECT_URI}
      ```  

4. Build the project:  
   ```bash  
   mvn clean install  
   ```  

5. Run database migrations:  
   ```bash  
   mvn flyway:migrate  
   ```  

6. Start the application:  
   ```bash  
   mvn spring-boot:run  
   ```  

7. Access the API at `http://localhost:8080`.  

## API Documentation  
Detailed API documentation (Swagger UI) will be available at:  
```
http://localhost:8080/swagger-ui.html  
```  

## License  
This project is developed exclusively for a local customer and is not intended for public use. This repository exists solely to showcase the code. No permission is granted for redistribution, modification, or commercial use.