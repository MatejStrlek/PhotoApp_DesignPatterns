# 📸 Photo Upload & Management Application

This project is developed as part of the **Advanced Application Development Based on Development Templates**. It is a fully functional photo-sharing web application built using **Spring Boot (MVC, Security, Scheduling)** and **Thymeleaf**. It supports multiple user types, package-based upload limits, image storage (local/Cloudinary), and detailed design pattern usage throughout the layers.


## 🚀 Features Overview

### 👥 User Accounts

- **Anonymous users**: Can browse uploaded photos.
- **Registered users**: Can upload photos, update metadata, and request a package change.
- **Administrators**: Can manage all photos and users, view user actions and statistics.

### 🔐 Authentication

- **Local login & registration**
- **Google and GitHub login** (via Spring Security OAuth2)
- **Package selection on registration**: FREE, PRO, or GOLD

## 🖼 Photo Upload & Browsing

- Upload photos with:
  - Description
  - Hashtags (CSV)
  - File format selection (JPG, PNG)
  - Processing options (resize)
- Save both **original** and **processed** versions
- **Browse** all uploaded photos
- **Edit metadata** (registered users only)
- **Thumbnail view** of the 10 most recent uploads

## 🔍 Search & Download

- Search by:
  - Hashtags
  - Upload date range
  - Author
  - File size range
- Download options:
  - Original image
  - Processed version (with filters)

## 📦 Packages & Limits

- Users can choose between:
  - **FREE**, **PRO**, or **GOLD** packages
- Each package defines:
  - Max upload size
  - Daily upload limit
- Users can **request a package change once per day**
- Changes take effect the next day
- Scheduled task processes requests daily

## ⚙️ Configuration

- **Photo storage** supports:
  - Local file system (`@Profile("local")`)
  - Cloudinary (`@Profile("cloudinary")`)
- Active profile is set via:  
  `spring.profiles.active=local` or `cloudinary`
- User actions are logged in audit logs

## 🧠 Design Patterns Used

This project includes the use of multiple **design patterns**, both explicitly implemented and implicitly used via Spring:

| Pattern                    | Layer                     | How It's Used                                                                 |
|----------------------------|----------------------------|-------------------------------------------------------------------------------|
| **Strategy**               | Business / Service Layer   | `ImageStorageStrategy` and `PackageLimitStrategy` to switch logic per profile/package |
| **Factory Method**         | Business Logic             | `PackageLimitStrategyFactory` returns correct upload strategy based on package type |
| **Command**                | Scheduling Layer           | `ChangeUserPackageCommand` encapsulates the action to update user packages daily |
| **Builder**                | Service Layer              | Used in `MyUserDetailsService` to construct `UserDetails` with chained methods |
| **Singleton**              | All Layers (Spring)        | Spring-managed beans (`@Service`, `@Component`, `@Repository`) are singletons |
| **Decorator**              | Business Logic             | Used in a decorator package to extend behavior for photo objects |
| **Iterator**               | Presentation (Thymeleaf)   | Used in `.html` views with `th:each` for looping photos and packages |
| **Chain of Responsibility**| Security Layer (Spring)    | Spring Security filter chain processes requests step-by-step |
| **Proxy**                  | Service Layer (Spring)     | Spring AOP proxies handle `@Transactional` and `@Async` logic transparently |

## 🧪 How to Run Locally

### ⚙️ 1. Clone the Project

```bash
git clone https://github.com/MatejStrlek/PhotoApp_DesignPatterns.git
cd PhotoApp_DesignPatterns
```

### 🔐 Step 2: Configure API Keys and Secrets

Add the following values to `src/main/resources/application.properties`

#### 🟦 GitHub OAuth

Create an OAuth app at [GitHub Developer Settings](https://github.com/settings/developers) → OAuth Apps, and add:

```bash
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
```

#### 🟥 Google OAuth

Create an OAuth 2.0 Client ID at [Google Cloud Console](https://console.cloud.google.com/apis/credentials) → Credentials, and add:

```bash
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

#### ☁️ Cloudinary (Optional)

Sign up at [cloudinary.com](cloudinary.com) and get your cloud name, API key, and secret. Add these values:

```bash
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

### 🌍 Step 3: Set Active Spring Profile

Choose a storage strategy by setting the active profile in `application.properties`:

```bash
spring.profiles.active=local        # For local file storage
# or
spring.profiles.active=cloudinary   # For Cloudinary cloud storage
```

### ▶️ Step 4: Run the Application

Start the Spring Boot application `PhotoAppDesignPatternsGalicApplication`.

Open your browser and go to: [http://localhost:8081](http://localhost:8081)

## 🐳 Docker Support

This project is fully Dockerized using Podman (a Docker-compatible container engine). To build and run the application in Podman/Docker, follow these steps:

Make sure your Spring Boot app is packaged:

```bash
mvn clean package
```

Then build the Podman image:

```bash
podman build -t photoapp-designpatterns .
```

Run the Podman container:

```bash
podman run -p 8081:8081 photoapp-designpatterns
```

You can now access the application at [http://localhost:8081](http://localhost:8081).

# Testing Concepts

### Introduction
Software testing is the process of evaluating an application to ensure it meets the expected 
requirements and performs reliably under various conditions.

### Purpose of Testing
Testing serves to:
- Detect **bugs and errors** early in the development cycle
- Validate that the system conforms to both **specification** and **user needs**
- Improve **software quality** by ensuring functional and performance correctness
- Enable safer **refactoring** and reduce long-term development costs

### Testing Levels
We follow a layered testing strategy, applying multiple testing levels:

| Level               | Purpose                                                                 |
|--------------------|-------------------------------------------------------------------------|
| **Unit Testing**     | Tests individual components (e.g. methods) in isolation using mocks     |
| **Integration Testing** | Verifies interactions between components (e.g., Controller + Service) |
| **System Testing**     | Ensures the application works as a whole                              |
| **Acceptance Testing** | Confirms that the system meets the customer’s requirements             |

These levels help uncover issues at different stages of development.

### Unit Testing
Unit testing focuses on verifying individual modules in isolation. It follows a **white-box** approach, 
meaning the developer tests internal logic directly. Characteristics:
- Written and maintained by developers
- No interaction with databases or external systems
- Encourages smaller, tighter, decoupled code
- Supports continuous integration practices

Frameworks and tools used:
- JUnit – for test structure and assertions
- Mockito – for mocking dependencies like repositories

Example: `ConsumptionServiceTest` ensures that `getMaxUploadSizeMb()` correctly calculates the maximum 
upload size based on the user's package.

### Integration Testing
Once individual units are tested, **integration testing** checks whether they work correctly together. It:
- Validates component interactions (e.g., controller + service + repository)
- Helps uncover issues like misconfigured beans, serialization problems and business logic integration bugs

We use:
- `@SpringBootTest` to load the full application context
- Embedded H2 database for realistic data flow

### UI / Controller Testing
We test the controller layer using:
- **MockMvc** with `@WebMvcTest`

We verify:
- Correct view names
- HTTP response status
- Form submission handling

This simulates browser requests to ensure the frontend and backend interact correctly.

### Testing Methodologies
#### 🔳 Black-box Testing
- No knowledge of code internals
- Based on expected input/output behavior
- Applied in UI testing and acceptance testing

#### ⚪ White-box Testing
- Based on internal logic and structure
- Used in unit testing (e.g., branch coverage, conditions)

### Verification vs Validation
- **Verification**: "Are we building the product right?" (meets the spec)
- **Validation**: "Are we building the right product?" (meets user need)

We apply both principles: verifying implementation matches the spec and validating user workflows through UI testing.

### Mocking

Mocking is the process of simulating external dependencies or behaviors:
- Allows isolated testing (e.g., no real DB)
- Enables inspection of method calls and arguments

We use Mockito to:
- Replace `UserRepository`, `PhotoRepository`, etc., with fake implementations
- Focus tests purely on logic, not external effects

### Test Case Design
Each test case follows this structure:
- What is being tested (requirement)
- Test data used
- Steps to execute the test
- Expected result
- Pass/fail condition

### Summary
We designed our testing approach around best practices. It ensures:
- Code is reliable and robust
- Components are loosely coupled and testable
- Features behave correctly across all layers
- Refactoring can be done safely

This approach helps us maintain high quality as the project evolves.

### Testing the Application
The application uses a layered testing approach to ensure correctness and stability. Unit tests validate 
individual services and strategies using JUnit and Mockito. Integration tests verify the collaboration between 
controllers, services and repositories using `@SpringBootTest` and `MockMvc`. UI (web layer) tests are handled 
using `@WebMvcTest`. This multi-level approach ensures that business logic, security rules, 
and form validation work as expected.

### Compatibility with Forms
Forms such as photo upload and package selection are tested to ensure compatibility with HTML input 
and validation. Tests simulate form submissions using MockMvc with `.param(...)` and `.multipart(...)` for file 
uploads. Spring's binding mechanism is validated through model attribute checks 
(`attributeExists("photoUploadDTO")`), and CSRF protection is integrated in POST tests using `.with(csrf())`.

### Performance and Memory Improvements
- Streaming image uploads: Instead of storing uploaded files fully in memory, images are processed using 
buffered streams and `ImageIO`, which reduces memory pressure
- Cloud storage integration - images are offloaded to Cloudinary, reducing local 
disk I/O and memory retention
- Avoiding eager loading - repositories are optimized to fetch only necessary data, reducing object loading
- Selective DTO usage - large objects like Photo are only partially mapped into DTOs for rendering, avoiding
unnecessary object construction and JSON conversion

## Metrics and Monitoring of the Application
This project integrates Spring Boot Actuator and Micrometer to provide real-time monitoring and 
observability of application health and performance.

### Health monitoring
The application exposes a `/actuator/health` endpoint that provides insights into the health of various components:
- Component availability (DB, disk, etc.)
- Startup health details
- Configurable readiness probes

```bash
curl http://localhost:8081/actuator/health
```

### Metrics monitoring
The following metrics are automatically collected and exposed:

| Metric name            | Description                        |
|------------------------|------------------------------------|
| `http.server.requests` | Counts and times all HTTP requests |
| `jvm.memory.used`      | Tracks used memory per JVM region  |
| `process.uptime`       | Application uptime in seconds      |
| `logback.events`       | Counts logging events by level     |
| `system.cpu.usage`     | Real-time system CPU usage         |

### Custom metric - `photo.uploads.total`
This custom metric tracks the total number of photos uploaded by users. 
It is incremented each time a photo is successfully uploaded.

View the metric via the `/actuator/metrics/photo.uploads.total` endpoint:
```bash
curl http://localhost:8081/actuator/metrics/photo.uploads.total
```

## SOLID principles
I wrote my code to comply with all five SOLID principles, improving maintainability, flexibility and testability:

### Single responsibility principle
- `PhotoUploadService`, `UploadMetricsService`, `AuditLoggerService` and etc. each have clear, distinct responsibilities

### Open-closed principle
- The strategy pattern (`ImageStorageStrategy`, `PackageLimitStrategyFactory`) allows adding new behaviors without
modifying existing logic

### Liskov substitution principle
- All `PackageLimitStrategy` implementations can be used interchangeably via the factory without altering behavior

### Interface segregation principle
- Interface Segregation Principle (ISP) - interfaces like `ImageStorageStrategy` are small and focused
- Clients implement just what they need

### Dependency inversion principle
- Services depend on strategy interfaces and abstract repositories, not concrete classes

> These refactorings support safer evolution of the codebase and enable easier unit testing and duplication detection.

## Functional programming

This project applies functional programming techniques in several methods to reduce coupling, improve readability
and eliminate boilerplate code.

### Example 1 - using optional to eliminate null checks

```java
return userRepository.findByEmail(email)
    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
```

This replaces manual null checks with a fluent, exception-safe one-liner.

### Example 2 - cleaning hashtag input with streams

```java
List<String> hashtags = dto.getHashtags().stream()
.map(String::trim)
.filter(hashtag -> !hashtag.isEmpty())
.collect(Collectors.toList());
```

This stream operation trims each hashtag, filters out empty strings and collects the result into a list.

### Example 3 - functional style in scheduled task (Command pattern)

```java
public void processPackageChangeRequests() {
    requestRepository
        .findAll()
        .forEach(request -> {
            new ChangeUserPackageCommand(
                request.getUser(),
                request.getRequestedPackageType(),
                userRepository
            ).execute();
            requestRepository.delete(request);
        });
}
```

This example uses a functional approach to iterate over package change requests, 
encapsulating the logic in a command object.

### Example 4 - smart defaulting with `orElseGet` for consumption tracking

```java
Consumption consumption = consumptionRepository
    .findByUserAndDate(user, today)
    .orElseGet(() -> new Consumption(user, today, 0, 0));
```

This avoids null checks by providing a default (newly created) `Consumption` object if none exists for the user today.

### Example 5 - Stream based filtering and mapping to detect heavy users

```java
public List<User> getHeavyUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> {
                    PackageLimitStrategy userStrategy = packageLimitStrategyFactory.getPackageLimitStrategy(user.getPackageType());
                    return consumptionRepository.findByUserAndDate(user, LocalDate.now())
                            .map(consumption ->
                                    consumption.getDailyUploadCount() >= userStrategy.getDailyUploadLimit() * .75
                            )
                            .orElse(false);
                })
                .toList();
    }
```

This method uses streams to filter users based on their upload consumption and flags those who are approaching their 
daily limit. It combines functional programming with the repository pattern to keep the code clean and expressive.