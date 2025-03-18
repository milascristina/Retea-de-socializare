# Social Network Application

This JavaFX-based social network application allows users to manage friendships, send messages, and interact in real-time. The application follows a layered architecture and uses PostgreSQL as the database.

## Features

### 1. **User Authentication**
- Login using first name, last name, and password.
- Upon login, users see their friend list and pending friend requests.
- If there are pending friend requests, a notification window appears. The user can close it manually or it disappears after a few seconds.

### 2. **Main User Interface (Post Login)**
After logging in, the user interface is split into two main sections:

#### **Left Section: Friend List & Actions**
- A table displays the user’s friends, including:
  - **ID**
  - **First Name**
  - **Last Name**
  - **Date of Friendship**
- Below the table, there are two buttons:
  - **Delete**: Removes the selected user from the friend list.
  - **Chat**: Opens a new chat window with the selected user.

#### **Right Section: Friend Management & Requests**
- **Friend Pagination**: A button at the top allows paginated viewing of friends.
- **Add Friend**:
  - Two input fields for the first name and last name.
  - A button labeled "Add New Friend" to send a friend request.
- **Pending Friend Requests**:
  - A table lists pending friend requests.
  - Below, an "Accept" button allows accepting a selected request.
  - When a request is accepted, the tables update in real-time.

### 3. **Chat System**
- Clicking "Chat" opens a new chat window with the selected friend.
- Displays previous messages between users.
- Users can type a new message in a text field at the bottom.
- A "Send" button sends the message.
- Messages update in real-time using the Observer pattern.

### 4. **Real-Time Updates**
- Friend lists, requests, and messages update in real-time using the Observer pattern.

## Technologies Used
- **JavaFX** for the graphical interface.
- **Java** for backend logic.
- **PostgreSQL** for database storage.
- **JDBC** for database communication.
- **Observer Pattern** for real-time updates.

## How to Run the Application
1. Clone the repository.
2. Set up a PostgreSQL database and update the connection details in the code.
3. Run the JavaFX application.

## Future Enhancements
- Improve UI design with CSS styling.
- Implement a search feature for friends.
- Add group chat functionality.

This application provides a simple yet powerful way to manage social connections and communicate in real-time. 🚀

