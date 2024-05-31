# Messenger Client

This repository contains the client-side implementation of a messaging application. The client connects to the server, allows users to send and receive messages in real-time, and manages user authentication.

## Features

- **Real-time Messaging**: Communicates with the server using WebSocket for real-time message exchange.
- **User Authentication**: Allows users to log in and manage sessions.
- **User Interface**: Provides a user-friendly interface for chatting and managing contacts.
- **Configuration Page**: Allows configuration of server connection settings such as server URL and port.

### Client Application Structure

- **Main Application**: `ClientApp.java` initializes and starts the client.
- **Controllers**: Handles UI interactions.
  - `ConfigController.java`: Manages the configuration page.
  - `ClientController.java`: Manages the main client interface.
- **Models**: Contains the client logic.
  - `Client.java`: Core client functionalities.
- **Views**: FXML files for UI.
  - `client.fxml`: Main client interface layout.

## Related Projects

- **Server Application**: The server-side implementation can be found [here](https://github.com/ituvtu/Server-JavaFX-WebSocket).
