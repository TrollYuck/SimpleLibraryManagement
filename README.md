# Library Management System   
This is an extremely simple Library Management System application written in Java. The application uses JDBC to connect to a MySQL database running in a Docker container.  
## Project Structure   
The project consists of the following main files:   
- App.java: The entry point of the application.   
- Client.java: Handles user interactions and operations. Apologies for the size and complexity of this file.   
- PasswordUtils.java: Provides utility methods for hashing and verifying passwords using BCrypt.   
- DatabaseConnection.java: Provides a method to establish a connection to the database. This file is not included, but it uses basic JDBC to connect to a MySQL server.   
## Database Connection   
The DatabaseConnection.java file is responsible for establishing a connection to the MySQL database. It uses JDBC to connect to a MySQL server running in a Docker container.   
## Dependencies
The project uses Maven for dependency management. The main dependencies are:  
- mysql-connector-java: MySQL JDBC driver
- jbcrypt: Library for hashing and verifying passwords
# Apology   
I apologize for the state of Client.java. It has grown quite large and complex. Future improvements will aim to refactor and simplify this file.  
