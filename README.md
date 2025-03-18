# Social Network Application

## Overview
This is a JavaFX-based social network application that allows users to log in, manage their friends, send and receive messages in real-time, and handle friend requests. The application follows a layered architecture and utilizes the Observer pattern for real-time updates.

## Features
- **User Authentication:** Log in using first name, last name, and password.
- **Friend Management:** View a table of friends with the option to delete or chat.
- **Real-Time Chat:** Open a new window for messaging, displaying previous messages, and sending new ones.
- **Paginated Friend List:** View friends in a paginated manner.
- **Friend Requests:** Add new friends by entering their name and last name, view incoming friend requests, and accept them.
- **Notifications:** If there are pending friend requests, a notification window appears upon login, disappearing after a few seconds if not manually closed.
- **Observer Pattern:** Messages update in real-time across all open chat windows.

## Technologies & Architecture
- **Programming Language:** Java
- **GUI Framework:** JavaFX
- **Database:** PostgreSQL
- **Persistence:** JDBC
- **Architecture:** Layered architecture with the following layers:
  - **Domain Layer:** Defines core entities and their validation.
  - **Repository Layer:** Handles data persistence and retrieval from the PostgreSQL database.
  - **Service Layer:** Implements business logic and coordinates between the repository and UI layers.
  - **UI Layer:** Manages user interactions and displays data in JavaFX components.
- **Design Pattern:** Model-View-Controller (MVC) used for structuring the UI and logic.
- **Real-time Updates:** Implemented using the **Observer Pattern** for dynamic message synchronization.

## Installation & Setup
1. Install **PostgreSQL** and create a database.
2. Update the database connection details in `UserDBRepository.java`.
3. Import the project into an IDE (such as IntelliJ IDEA or Eclipse).
4. Run the application from the main class.

## Usage
1. Log in with an existing user.
2. Manage your friends by adding, deleting, or viewing requests.
3. Open chat with a friend to send and receive messages in real-time.
4. Accept or decline friend requests directly from the interface.


## Screenshots
![Screenshot 2025-03-18 140146](https://github.com/user-attachments/assets/b5a9b54c-e4a9-4fc1-a5e8-a3b10384dde8)
