# E-commerce Project with Product Reviews

This is a Spring Boot project that implements a product review and rating system for an e-commerce platform.

## Prerequisites

- Java 21
- Maven
- MySQL 8.0

## Database Configuration

The application is configured to connect to MySQL with the following settings:
- Host: localhost
- Port: 3306
- Database: lab4db
- Username: phucdb
- Password: 77777

## Running the Application

1. Make sure MySQL is running and the database credentials are correct
2. Navigate to the project directory
3. Run the following command:
   ```
   mvn spring-boot:run
   ```

The application will start on port 8080.

## Features

- Product review system with ratings (1-5 stars)
- Users can only review products they have purchased
- Automatic calculation of average product ratings
- Review validation and duplicate review prevention

## API Endpoints

- `POST /api/reviews` - Create a new review
  - Requires authentication
  - Request body:
    ```json
    {
      "product": {
        "id": 1
      },
      "rating": 5,
      "comment": "Great product!"
    }
    ```
