# Food Delivery Application 🍔🚀

Welcome to the **Food Delivery Application**, a robust and scalable platform designed to streamline the food ordering and delivery process. Built with **Spring Boot**, **PostgreSQL**, and **AWS S3**, this application offers a seamless experience for customers, restaurant owners, delivery personnel, and administrators. With features like user authentication, cart management, order tracking, and dynamic delivery fee calculation using the Google Maps API, it showcases modern web development practices and a user-centric design.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
- [Testing with Postman](#testing-with-postman)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [Contact](#contact)

---

## Project Overview

The Food Delivery Application is a full-stack web application designed to connect customers with restaurants, enabling food ordering, delivery management, and restaurant discovery. It supports multiple user roles (Customer, Restaurant Owner, Delivery Person, Admin) and includes advanced features like dynamic delivery fee calculation based on geographic coordinates, image uploads to AWS S3, and email notifications for order and delivery updates.

This project demonstrates proficiency in **Spring Boot**, **RESTful APIs**, **JPA/Hibernate**, **Spring Security**, and integration with third-party services like AWS S3 and Google Maps API. The codebase is modular, maintainable, and follows best practices for scalability and security.

---

## Key Features

- **User Authentication**: Secure registration and login with JWT-based authentication.
- **Role-Based Access**: Supports multiple roles:
  - **Customer**: Browse restaurants, add items to cart, place orders, track deliveries, and manage favorite restaurants.
  - **Restaurant Owner**: Manage restaurants, add/update food items, and track orders.
  - **Delivery Person**: View assigned deliveries and update delivery status.
  - **Admin**: Assign delivery personnel and manage delivery statuses.
- **Cart Management**: Add, update, remove, and clear cart items.
- **Order Management**: Create orders from cart, track order status, and cancel orders.
- **Delivery Management**: Create deliveries, assign delivery personnel, and track delivery status with status history.
- **Restaurant Discovery**: Search restaurants by keyword, cuisine type, or dietary preference, with sorting by name or rating.
- **Reviews and Ratings**: Customers can leave reviews and ratings for food items, with aggregated restaurant ratings.
- **Image Uploads**: Restaurant owners can upload images for restaurants and food items to AWS S3.
- **Email Notifications**: Automated notifications for order and delivery status updates.
- **Delivery Fee Calculation**: Dynamic calculation of delivery fees based on the distance between customer and restaurant coordinates using the Google Maps Distance Matrix API.

---

## Technology Stack

| **Category**         | **Technologies**                              |
|-----------------------|-----------------------------------------------|
| **Backend**           | Spring Boot, Spring Data JPA, Spring Security |
| **Database**          | PostgreSQL                                    |
| **Authentication**    | JWT (JSON Web Tokens)                        |
| **File Storage**      | AWS S3                                       |
| **External APIs**     | Google Maps Distance Matrix API              |
| **Email Service**     | Spring Mail (SMTP with Gmail)                |
| **Build Tool**        | Maven                                        |
| **Java Version**      | Java 17                                      |

---

## Architecture

The application follows a **layered architecture** to ensure modularity and maintainability:

- **Controller Layer**: Handles HTTP requests and responses, with role-based access control using Spring Security.
- **Service Layer**: Contains business logic, including cart management, order processing, delivery fee calculation, and notification services.
- **Repository Layer**: Interacts with the PostgreSQL database using Spring Data JPA.
- **Model Layer**: Defines entities like `User`, `Restaurant`, `FoodItem`, `Order`, `Delivery`, and `CartItem`, with appropriate JPA annotations.
- **Configuration**: Uses `application.properties` for environment-specific settings, including database, AWS S3, Google Maps API, and email configurations.

The application integrates with **AWS S3** for image storage and **Google Maps API** for distance-based delivery fee calculations. It uses **Spring Security** for JWT authentication and role-based authorization.

---

## Setup Instructions

Follow these steps to set up and run the application locally:

### Prerequisites
- **Java 17** (JDK)
- **Maven** (for dependency management)
- **PostgreSQL** (version 12 or higher)
- **AWS S3 Account** (with access key and secret key)
- **Google Maps API Key** (with Distance Matrix API enabled)
- **Gmail Account** (for email notifications)

### Steps
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd FoodDelivery
   ```

2. **Set Up PostgreSQL**:
   - Create a database named `food_delivery`.
   - Update `application.properties` with your PostgreSQL credentials:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/food_delivery
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. **Configure AWS S3**:
   - Update `application.properties` with your AWS credentials and bucket name:
     ```properties
     aws.accessKeyId=your_access_key
     aws.secretKey=your_secret_key
     aws.region=your_region
     aws.s3.bucketName=your_bucket_name
     ```

4. **Configure Google Maps API**:
   - Obtain a Google Maps API key from the [Google Cloud Console](https://console.cloud.google.com/).
   - Add the key to `application.properties`:
     ```properties
     google.maps.api.key=your_google_maps_api_key
     ```

5. **Configure Email Notifications**:
   - Update `application.properties` with your Gmail credentials:
     ```properties
     spring.mail.username=your_email@gmail.com
     spring.mail.password=your_app_specific_password
     ```

6. **Install Dependencies**:
   ```bash
   mvn clean install
   ```

7. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

8. **Database Initialization**:
   - The application uses `spring.jpa.hibernate.ddl-auto=update` to automatically create/update database tables based on entity definitions.

---

## API Endpoints

Below are the key API endpoints for testing the application. All endpoints require JWT authentication unless specified otherwise. Obtain a JWT token by registering and logging in via the authentication endpoints.

| **Endpoint**                     | **Method** | **Description**                              | **Role**             | **Request Body**                          |
|----------------------------------|------------|----------------------------------------------|----------------------|-------------------------------------------|
| `/api/auth/register`             | POST       | Register a new user                          | Public               | `{ "email": "", "password": "", "name": "", "role": "CUSTOMER/RESTAURANT_OWNER/DELIVERY_PERSON/ADMIN" }` |
| `/api/auth/login`                | POST       | Login and receive JWT token                  | Public               | `{ "email": "", "password": "" }`         |
| `/api/users/me`                  | GET        | Get current user details                     | All roles            | None                                      |
| `/api/users/me/coordinates`      | PUT        | Update user coordinates                      | All roles            | `{ "latitude": 0.0, "longitude": 0.0 }`   |
| `/api/restaurants`               | POST       | Create a new restaurant                      | RESTAURANT_OWNER     | `{ "name": "", "location": "", "cuisineType": "", "dietaryPreference": "", "coordinates": { "latitude": 0.0, "longitude": 0.0 } }` |
| `/api/restaurants/my`            | GET        | Get restaurants owned by the user            | RESTAURANT_OWNER     | None                                      |
| `/api/food-items`                | POST       | Create a new food item                       | RESTAURANT_OWNER     | `{ "name": "", "description": "", "price": 0.0, "restaurantId": 0 }` (multipart with optional `image`) |
| `/api/cart/items`                | POST       | Add item to cart                            | CUSTOMER             | `{ "foodItemId": 0, "quantity": 0 }`      |
| `/api/cart`                      | GET        | Get cart contents with total price          | CUSTOMER             | None                                      |
| `/api/orders/from-cart`          | POST       | Create order from cart with delivery fee    | CUSTOMER             | None                                      |
| `/api/orders/my`                 | GET        | Get customer orders                         | CUSTOMER             | None                                      |
| `/api/deliveries/{orderId}`      | GET        | Get delivery details for an order            | CUSTOMER             | None                                      |
| `/api/delivery-person/my-deliveries` | GET    | Get assigned deliveries                     | DELIVERY_PERSON      | None                                      |
| `/api/discovery/search`          | GET        | Search restaurants                          | CUSTOMER             | Query params: `keyword`, `cuisineType`, `dietaryPreference`, `sortBy` |
| `/api/reviews/food-item/{foodItemId}` | POST | Submit a review for a food item             | CUSTOMER             | `{ "rating": 1-5, "comment": "" }`        |

### Delivery Fee Calculation
The delivery fee is calculated dynamically based on the distance between the customer's and restaurant's coordinates using the Google Maps Distance Matrix API. The fee is computed as `$0.5 per kilometer`. The customer and restaurant coordinates are set via the respective update endpoints.

---

## Testing with Postman

To test the API endpoints, follow these steps in Postman:

1. **Register a User**:
   - **URL**: `POST http://localhost:8080/api/auth/register`
   - **Body** (JSON):
     ```json
     {
       "email": "customer@example.com",
       "password": "password123",
       "name": "John Doe",
       "role": "CUSTOMER"
     }
     ```
   - **Response**: User details

2. **Login to Get JWT Token**:
   - **URL**: `POST http://localhost:8080/api/auth/login`
   - **Body** (JSON):
     ```json
     {
       "email": "customer@example.com",
       "password": "password123"
     }
     ```
   - **Response**: JWT token (e.g., `eyJhbGciOiJIUzI1NiIs...`)
   - Copy the token for subsequent requests.

3. **Set User Coordinates**:
   - **URL**: `PUT http://localhost:8080/api/users/me/coordinates`
   - **Headers**: `Authorization: Bearer <JWT_TOKEN>`
   - **Body** (JSON):
     ```json
     {
       "latitude": 37.7749,
       "longitude": -122.4194
     }
     ```
   - **Response**: Updated user details

4. **Create a Restaurant (as Restaurant Owner)**:
   - Register and login as a `RESTAURANT_OWNER`.
   - **URL**: `POST http://localhost:8080/api/restaurants`
   - **Headers**: `Authorization: Bearer <JWT_TOKEN>`
   - **Body** (multipart/form-data):
     - Key: `restaurant`, Value (JSON):
       ```json
       {
         "name": "Tasty Bites",
         "location": "123 Main St",
         "cuisineType": "Italian",
         "dietaryPreference": "Vegetarian",
         "coordinates": { "latitude": 37.7849, "longitude": -122.4094 }
       }
       ```
     - Key: `image`, Value: (optional image file)
   - **Response**: Created restaurant details

5. **Add Food Item to Cart**:
   - **URL**: `POST http://localhost:8080/api/cart/items`
   - **Headers**: `Authorization: Bearer <JWT_TOKEN>`
   - **Body** (JSON):
     ```json
     {
       "foodItemId": 1,
       "quantity": 2
     }
     ```
   - **Response**: Cart item details

6. **Create Order from Cart**:
   - **URL**: `POST http://localhost:8080/api/orders/from-cart`
   - **Headers**: `Authorization: Bearer <JWT_TOKEN>`
   - **Body**: None
   - **Response**: Order details with delivery fee

7. **Track Order**:
   - **URL**: `GET http://localhost:8080/api/orders/{orderId}/track`
   - **Headers**: `Authorization: Bearer <JWT_TOKEN>`
   - **Response**: Order and delivery tracking details

---

## Future Enhancements

- **Payment Integration**: Add support for payment gateways like Stripe or PayPal.
- **Real-Time Tracking**: Implement WebSocket-based real-time delivery tracking.
- **Advanced Search**: Enhance restaurant discovery with geolocation-based filtering.
- **Promotions and Discounts**: Add support for promotional codes and discounts.
- **Mobile App**: Develop a frontend mobile application for a better user experience.

---

## Contributing

Contributions are welcome! To contribute:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add your feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.

Please ensure your code follows the project's coding standards and includes appropriate tests.

---

## Contact

For any questions or feedback, please contact:

- **Email**: edwinjoel1204@gmail.com
- **GitHub**: [Your GitHub Profile](#)
- **Project Repository**: [FoodDelivery Repository](#)

---

**Bon Appétit! Enjoy building and exploring the Food Delivery Application!** 😋
