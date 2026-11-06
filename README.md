# ShopSphere – E-Commerce Backend System

A RESTful E-Commerce Backend application built using **Java, Spring Boot, Spring Security, JWT, Spring Data JPA, and MySQL**. ShopSphere provides backend functionality for an online shopping platform, including product management, customer authentication, shopping cart operations, checkout, and order management.

## Features

### Authentication & Authorization
- Customer registration and login.
- JWT-based stateless authentication.
- Role-based access control for Admin and Customer.
- Password encryption using Spring Security.

### Product Management
- Create, retrieve, update, and delete products (Admin).
- Browse products (Public).
- Product search by name.
- Pagination and sorting.
- Request validation and centralized exception handling.

### Shopping Cart
- Add products to cart.
- Update product quantities.
- Remove individual items.
- Clear the cart.
- Calculate cart totals.
- Validate product availability.

### Order Management
- Place orders directly.
- Checkout using cart items.
- View order history and individual orders.
- Cancel eligible orders.
- Track order status.
- Validate stock and update inventory during order placement.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Programming language |
| Spring Boot | Backend framework |
| Spring Security | Application security |
| JWT | Authentication |
| Spring Data JPA | Database operations |
| Hibernate | ORM |
| MySQL | Relational database |
| Maven | Build and dependency management |
| Postman | API testing |
| IntelliJ IDEA | Development environment |

## Architecture

The application follows a layered architecture:

- **Controller Layer:** Handles HTTP requests and responses.
- **Service Layer:** Implements business logic.
- **Repository Layer:** Communicates with the database.
- **Entity Layer:** Represents database tables.
- **DTO Layer:** Manages API request and response data.
- **Security Layer:** Handles JWT authentication and authorization.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register customer |
| POST | `/api/auth/login` | Login and obtain JWT |

### Products

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create product (Admin) |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update product (Admin) |
| DELETE | `/api/products/{id}` | Delete product (Admin) |
| GET | `/api/products/page` | Paginated and sorted products |
| GET | `/api/products/search` | Search products by name |

### Cart

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/cart/items` | Add product to cart |
| GET | `/api/cart` | View cart |
| PUT | `/api/cart/items/{productId}` | Update cart quantity |
| DELETE | `/api/cart/items/{productId}` | Remove cart item |
| DELETE | `/api/cart` | Clear cart |

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Place an order |
| POST | `/api/orders/checkout` | Checkout using cart |
| GET | `/api/orders` | View customer orders |
| GET | `/api/orders/{orderId}` | Get order details |
| PUT | `/api/orders/{orderId}/cancel` | Cancel eligible order |

### Admin Orders

| Method | Endpoint | Description |
|---|---|---|
| PUT | `/api/admin/orders/{orderId}/status` | Update order status (Admin) |

## Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE ecommerce_db;
```

Configure the following environment variables in your IDE:

```text
DB_URL=jdbc:mysql://localhost:3306/ecommerce_db
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_secure_jwt_secret
```

The application reads these values through environment variables. **Never commit actual passwords or JWT secrets to GitHub.**

## How to Run

1. Clone the repository:

```bash
git clone https://github.com/mounikamalineni26/shopsphere-ecommerce-backend.git
```

2. Open the project in IntelliJ IDEA.

3. Configure the environment variables listed above.

4. Make sure MySQL is running.

5. Run the main Spring Boot application:

```text
EcommerceBackendApplication.java
```

6. The application starts at:

```text
http://localhost:8080
```

Use Postman to test the REST APIs. For protected endpoints, include your JWT token in the Authorization header as a Bearer token.

## Future Improvements

- Improve inventory consistency and concurrent purchase handling.
- Add automated unit and integration tests.
- Integrate Swagger/OpenAPI documentation.
- Dockerize and deploy the application.

## Author

**Mounika Malineni**

- GitHub: [mounikamalineni26](https://github.com/mounikamalineni26)
- LinkedIn: [mounikamalineni](https://www.linkedin.com/in/mounikamalineni/)
