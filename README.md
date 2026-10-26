# BeeStore

BeeStore is a Spring Boot REST API for managing a product catalog. It supports product creation, retrieval, updates, deletion, search, and stock-out queries. The project includes request validation, OpenAPI documentation, health monitoring, and automated tests with JaCoCo coverage reports.

## Technology stack

- Java 17
- Spring Boot 3.5.6
- Maven
- Spring Web and Spring Data JPA
- H2 for development and tests; MySQL for production
- springdoc OpenAPI / Swagger UI
- JUnit, Mockito, and Cucumber
- JaCoCo

## Requirements

- JDK 17 or later
- No separate Maven installation is required when using the included Maven Wrapper
- MySQL is required only when running with the `prod` profile

## Run locally

The default `dev` profile uses an in-memory H2 database. Start the application from the repository root:

```powershell
.\mvnw.cmd spring-boot:run
```

Or, on macOS/Linux:

```bash
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. The development H2 console is available at `http://localhost:8080/h2-console`.

The development database is recreated when the application stops, so its data is temporary.

## API

All product endpoints use the `/api/v1/produits` base path.

| Method | Endpoint | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/api/v1/produits` | Create a product | `201 Created` |
| `GET` | `/api/v1/produits` | List all products | `200 OK` |
| `GET` | `/api/v1/produits/{id}` | Get a product by ID | `200 OK` |
| `PUT` | `/api/v1/produits/{id}` | Update a product | `200 OK` |
| `DELETE` | `/api/v1/produits/{id}` | Delete a product | `204 No Content` |
| `GET` | `/api/v1/produits/search?categorie={value}` | Find products by category (case-insensitive) | `200 OK` |
| `GET` | `/api/v1/produits/search?nom={value}` | Find products whose names contain the value (case-insensitive) | `200 OK` |
| `GET` | `/api/v1/produits/rupture-stock` | List products with zero stock | `200 OK` |

If the search endpoint receives neither `categorie` nor `nom`, it returns all products. When both are supplied, category search takes precedence.

### Create a product

The `nom`, `prix`, and `stock` fields are required. The name must contain 2–100 characters, the price must be greater than zero, and stock must be zero or greater. `description` and `categorie` are optional; their maximum lengths are 500 and 100 characters, respectively. `actif` defaults to `true`.

```http
POST /api/v1/produits
Content-Type: application/json
```

```json
{
  "nom": "Clavier mécanique",
  "description": "Clavier mécanique compact",
  "prix": 89.9,
  "stock": 25,
  "categorie": "Informatique",
  "actif": true
}
```

The response includes the product ID and creation date in addition to its submitted fields. Invalid request data returns `400 Bad Request`; requesting or deleting a product that does not exist returns `404 Not Found`.

## API documentation and health

With the application running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Actuator health: `http://localhost:8080/actuator/health`

## Profiles and database configuration

The active profile is `dev` by default.

| Profile | Database | Schema behavior |
| --- | --- | --- |
| `dev` | In-memory H2 | Recreated on application start and dropped on shutdown |
| `test` | In-memory H2 | Recreated for tests |
| `prod` | MySQL | Hibernate updates the schema |

To run with the production profile, provide the database connection settings through environment variables; do not commit credentials:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/beestore"
$env:DB_USERNAME = "your-username"
$env:DB_PASSWORD = "your-password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=prod"
```

The production profile expects `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to be set.

## Tests and coverage

Run the test suite and build the project:

```powershell
.\mvnw.cmd clean verify
```

Or, on macOS/Linux:

```bash
./mvnw clean verify
```

The tests include service unit tests, controller integration tests, and a Cucumber product-creation scenario. JaCoCo generates its HTML report at `target/site/jacoco/index.html` and its XML report at `target/site/jacoco/jacoco.xml`. Coverage is reported during the test lifecycle; a coverage threshold check is not currently enabled.

## Project structure

```text
src/
├── main/
│   ├── java/com/beedigital/beestore/
│   │   ├── config/       # OpenAPI configuration
│   │   ├── controller/   # REST endpoints
│   │   ├── dto/          # API request and response models
│   │   ├── entity/       # JPA product entity
│   │   ├── exception/    # API errors and exception handling
│   │   ├── repository/   # Spring Data JPA repository
│   │   └── service/      # Product business logic
│   └── resources/        # Application and profile configuration
└── test/
    ├── java/             # Unit, integration, and Cucumber tests
    └── resources/        # Test configuration and feature scenarios
```
