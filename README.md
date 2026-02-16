# SYNK - Productivity & Habit Tracking Application

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![JUnit 5](https://img.shields.io/badge/Testing-JUnit_5-25A162.svg)](https://junit.org/junit5/)
[![OAuth 2.0](https://img.shields.io/badge/Security-OAuth_2.0-blue.svg)](https://oauth.net/2/)
[![Google Calendar API](https://img.shields.io/badge/API-Google_Calendar-4285F4.svg)](https://developers.google.com/calendar)


**SYNK** is a comprehensive desktop productivity application designed to help users manage tasks, build positive habits, and stay organized. Built with a focus on **Clean Architecture** and robust software design principles, SYNK integrates seamlessly with **Google Calendar** to keep your schedule in sync.

## Key Features

*   **Smart Task Management**: Create, modify, and organize daily tasks with ease.
*   **Habit Construction**: Track and build long-term habits with dedicated tools.
*   **Google Calendar Sync**: Two-way synchronization with your Google Calendar to ensure you never miss a deadline.
*   **Gamification**: Stay motivated with a built-in **Leaderboard** and **User Statistics** to track your progress.
*   **Secure Authentication**: Fully functional User Login and Signup system.
*   **Data Persistence**:  Reliable local data storage and cloud integration.

## Built With

*   **Language**: Java 17
*   **GUI Framework**: Java Swing
*   **Build Tool**: Maven
*   **APIs**: Google Calendar API, Google OAuth Client
*   **Testing**: JUnit 5
*   **Libraries**: org.json, JDatePicker, OkHttp

## Architecture & Design

This project serves as a practical implementation of **Clean Architecture** (Robert C. Martin), ensuring separation of concerns and maintainability.

*   **Entities**: Core business logic and rules, independent of frameworks.
*   **Use Cases**: Application-specific business rules (Interactors).
*   **Interface Adapters**: Controllers, Presenters, and ViewModels that convert data between the use cases and the view.
*   **Frameworks & Drivers**: The outer layer containing the UI (Swing), Database Access, and External APIs.

### Design Patterns Utilized
*   **Builder Pattern**: For complex object construction (e.g., `AppBuilder`).
*   **Strategy Pattern**: For interchangeable algorithms (sorting, filtering).
*   **Dependency Injection**: To decouple components and facilitate testing.
*   **Observer Pattern**: For reactive UI updates.

### Credentails Note 

  **Configure Credentials**
    *   Obtain **OAuth 2.0 Client Credentials** (`credentials.json`) from the [Google Cloud Console](https://console.cloud.google.com/).
    *   Place the file in `src/main/resources/credentials.json`.
    *   *Note: Ensure this file is listed in your `.gitignore` to prevent leaking sensitive keys.*


## Skills & Competencies Demonstrated

*   **Advanced Java Development**: Utilization of modern Java features and strong OOP principles.
*   **Architectural Design**: Strict adherence to **Clean Architecture** and **SOLID** principles for scalable software.
*   **API Integration**: Implementation of **OAuth 2.0** flows and RESTful integration with **Google Services**.
*   **Full-Cycle Development**: From requirements and design patterns to implementation, testing, and CI/CD configuration.
*   **GUI Programming**: Creating responsive and interactive desktop applications using Swing.
*   **Test-Driven Development**: Writing unit tests with JUnit/TestNG to ensure code reliability.

---
# App Overview 

## Interface Overview

| **Authentication & Navigation** | **Task & Habit Management** |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/260cb7a2-0589-48fc-bdff-33e9f61d96f6" width="400" alt="SYNK Account Creation and Signup Screen"> |  <img width="600" height="634" alt="image" src="https://github.com/user-attachments/assets/cb97f912-dd2d-49f1-a351-bf9aa8339a02" /> |
| *Seamless onboarding with a clean signup flow.* | *Manage daily tasks and recurring habits in one place.* |
| <img src="https://github.com/user-attachments/assets/5bc6d2a9-c32c-432a-a951-7ed7b2f5c8f1" width="400" alt="SYNK Home Dashboard with Navigation Menu"> | <img src="https://github.com/user-attachments/assets/7508634d-3d6e-4175-acfc-bfcfdcf36100" width="400" alt="SYNK Productivity Statistics Dashboard showing streaks and completion rates"> |
| *Personalized home screen for quick navigation.* | *Track your progress with real-time productivity metrics.* |
| <img src="https://github.com/user-attachments/assets/d1e82f03-b98a-4039-99ae-5fa84437492b" width="400" alt="Google OAuth Integration for SYNK CSC207"> | <img src="https://github.com/user-attachments/assets/305a7d9d-05a9-49e3-bbc9-b7cfbe14282b" width="400" alt="SYNK Habit Streak Leaderboard showing top users"> |
| *Secure integration with Google OAuth 2.0.* | *Stay motivated by competing on the global leaderboard.* |



---


*This project was developed as part of the collaborative software design curriculum (CSC207 at UofT).*
