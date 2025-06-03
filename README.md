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