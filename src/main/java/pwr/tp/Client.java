package pwr.tp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Client {
  private final Connection connection;
  private final Scanner scanner = new Scanner(System.in);

  private boolean loggedIn = false;
  private boolean running;

  private int userId = -1;
  private String name = "";
  private String mail = "";
  private String role = "";

  public Client(Connection connection) {
    this.connection = connection;
    running = true;
  }

  private void printWelcomeMessage() {
    System.out.println("\n" +
            "  _      _ _                           ____   _____ \n" +
            " | |    (_) |                         / __ \\ / ____|\n" +
            " | |     _| |__  _ __ __ _ _ __ _   _| |  | | (___  \n" +
            " | |    | | '_ \\| '__/ _` | '__| | | | |  | |\\___ \\ \n" +
            " | |____| | |_) | | | (_| | |  | |_| | |__| |____) |\n" +
            " |______|_|_.__/|_|  \\__,_|_|   \\__, |\\____/|_____/ \n" +
            "                                 __/ |              \n" +
            "                                |___/               \n");
    System.out.println("Welcome to the Library Management System!");
  }

  public void run() {
    printWelcomeMessage();
    while (running) {
      if (!loggedIn) {
        printLoginMenu();
      } else {
        printMainMenu();
      }
    }
  }

  private void printMainMenu() {
    System.out.println("Welcome " + name + "!");
    System.out.println("Please select an option:");
    System.out.println("1. List all available books");
    System.out.println("1a. List my books");
    System.out.println("2. Reserve a book");
    System.out.println("2a. List my reserved books");
    System.out.println("3. Borrow a book");
    System.out.println("4. Return a book");
    System.out.println("5. Logout");
    if (role.equals("admin")) {
      printAdminMainMenu();
    }
    String input = scanner.nextLine();
    int choice;
    switch (input) {
      case "1":
        listAllBooks();
        break;
      case "1a":
        listMyBooks();
        break;
      case "2":
        System.out.println("Enter the book id you want to reserve:");
        input = scanner.nextLine();
        input = input.trim();
        try {
          choice = Integer.parseInt(input);
          ReserveABook(choice);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "2a":
        listMyReservedBooks();
      case "3":
        System.out.println("Enter the book id you want to borrow:");
        input = scanner.nextLine();
        input = input.trim();
        try {
          choice = Integer.parseInt(input);
          borrowBook(choice);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "4":
        System.out.println("Enter the book id you want to return:");
        input = scanner.nextLine();
        input = input.trim();
        choice = Integer.parseInt(input);
        try {
          choice = Integer.parseInt(input);
          returnBook(choice);
          } catch (NumberFormatException e) {
          break;
        }
        break;
      case "5":
        loggedIn = false;
        break;
      case "6":
        System.out.println("Enter the books TITLE you want to add:");
        String title = scanner.nextLine();
        System.out.println("Enter the books AUTHOR you want to add:");
        String author = scanner.nextLine();
        System.out.println("Enter the books ISBN you want to add:");
        String isbn = scanner.nextLine();
        System.out.println("Enter the books PUBLICATION YEAR you want to add:");
        String year = scanner.nextLine();
        try {
          choice = Integer.parseInt(year);
          addBook(title, author, isbn, choice, 0);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "7":
        System.out.println("Enter the book id you want to add copies of:");
        String bookId = scanner.nextLine();
        System.out.println("Enter the number of copies you want to add:");
        String copies = scanner.nextLine();
        try {
          choice = Integer.parseInt(bookId);
          int copiesInt = Integer.parseInt(copies);
          addCopiesOfBook(choice, copiesInt);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "8":
        System.out.println("Enter the book id you want to remove copies of:");
        bookId = scanner.nextLine();
        System.out.println("Enter the number of copies you want to remove:");
        copies = scanner.nextLine();
        try {
          choice = Integer.parseInt(bookId);
          int copiesInt = Integer.parseInt(copies);
          removeCopiesOfBook(choice, copiesInt);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "bl":
        listOngoingBookLoans();
        break;
      case "rl":
        listAllReservations();
        break;
      case "f":
        listAllFines();
        break;
      case "c":
        System.out.println("Enter the user id you want to calculate fines for:");
        String userId = scanner.nextLine();
        try {
          choice = Integer.parseInt(userId);
          calculateFines(choice);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "gp":
        System.out.println("Enter the user id you want to give admin privileges to:");
        userId = scanner.nextLine();
        try {
          choice = Integer.parseInt(userId);
          givePrivileges(choice);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      case "rp":
        System.out.println("Enter the user id you want to remove admin privileges from:");
        userId = scanner.nextLine();
        try {
          choice = Integer.parseInt(userId);
          removePrivileges(choice);
        } catch (NumberFormatException e) {
          break;
        }
        break;
      default:
        System.out.println("Invalid input. Please try again.");
    }
  }

  private void listMyBooks() {
    String procedureCall = "{ CALL get_user_loans(?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameter
        callableStatement.setInt(1, userId);
        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            int loanId = resultSet.getInt("loan_id");
            int bookId = resultSet.getInt("book_id");
            String title = resultSet.getString("title");
            String loanDate = resultSet.getString("loan_date");
            String returnDate = resultSet.getString("return_date");

            System.out.println("| Loan ID: " + loanId + " | Book ID: " + bookId
                    + " | Title: " + title + " | Loan Date: " + loanDate
                    + " | Return Date: " + returnDate + " |");
          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to list books.");
      System.out.println(e.getMessage());
    }

  }


  private void listAllBooks() {
    String procedureCall = "{ CALL get_available_books() }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            int id = resultSet.getInt("book_id");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            int copies = resultSet.getInt("copies_available");
            System.out.println("| ID: " + id + " | Title: " + title
                    + " | Author: " + author + " | Copies: " + copies + " |");
          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to list books.");
      System.out.println(e.getMessage());
    }

  }

  private void ReserveABook(int bookId) {
    String procedureCall = "{ CALL reserve_book(?, ?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setInt(1, userId);
        callableStatement.setInt(2, bookId);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Book reserved successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to reserve book.");
      System.out.println(e.getMessage());
    }
  }

  private void listMyReservedBooks() {
    String procedureCall = "{ CALL get_user_reservations(?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameter
        callableStatement.setInt(1, userId);
        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            int reservationId = resultSet.getInt("reservation_id");
            int bookId = resultSet.getInt("book_id");
            String title = resultSet.getString("title");
            String reservationDate = resultSet.getString("reservation_date");

            System.out.println("| Reservation ID: " + reservationId + " | Book ID: " + bookId
                    + " | Title: " + title + " | Reservation Date: " + reservationDate + " |");
          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to list reservations.");
      System.out.println(e.getMessage());
    }

  }

  private void borrowBook(int bookId) {
    String procedureCall = "{ CALL loan_book(?, ?) }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setInt(1, userId);
        callableStatement.setInt(2, bookId);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Book borrowed successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to borrow book.");
      System.out.println(e.getMessage());
    }
  }

  private void returnBook(int bookId) {
    String procedureCall = "{ CALL return_book(?, ?) }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setInt(1, userId);
        callableStatement.setInt(2, bookId);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Book returned successfully!");
      }
    } catch (SQLException e) {
        System.out.println("Failed to return book.");
      System.out.println(e.getMessage());
    }
  }

  private void addBook(String title, String author, String isbn, int bookId, int copies) {
    String procedureCall = "{ CALL create_book(?, ?, ?, ?, ?) }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setString(1, title);
        callableStatement.setString(2, author);
        callableStatement.setString(3, isbn);
        callableStatement.setInt(4, bookId);
        callableStatement.setInt(5, copies);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Book: " + title + "added successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to add book.");
      System.out.println(e.getMessage());
    }
  }

  private void addCopiesOfBook(int bookId, int copies) {
    String procedureCall = "{ CALL add_copies(?, ?) }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setInt(1, bookId);
        callableStatement.setInt(2, copies);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Copies added successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to add copies.");
      System.out.println(e.getMessage());
    }
  }

  private void removeCopiesOfBook(int bookId, int copies) {
    String procedureCall = "{ CALL remove_copies(?, ?) }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setInt(1, bookId);
        callableStatement.setInt(2, copies);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Copies removed successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to remove copies.");
      System.out.println(e.getMessage());
    }
  }

  private void listOngoingBookLoans() {
    String procedureCall = "{ CALL get_all_loans() }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            int id = resultSet.getInt("loan_id");
            int bookId = resultSet.getInt("book_id");
            String title = resultSet.getString("title");
            String user_name = resultSet.getString("user_name");
            String loan_date = resultSet.getString("loan_date");
            String return_date = resultSet.getString("return_date");

            System.out.println("| Loan ID: " + id + " | Book ID: " + bookId
                    + " | Title: " + title + " | User: "
                    + user_name + " | Loan Date: " + loan_date
                    + " | Return Date: " + return_date + " |");

          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to list loans.");
      System.out.println(e.getMessage());
    }
  }

  private void listAllReservations() {
    String procedureCall = "{ CALL get_all_reservations() }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            int id = resultSet.getInt("reservation_id");
            int bookId = resultSet.getInt("book_id");
            String title = resultSet.getString("title");
            String user_name = resultSet.getString("user_name");
            String reservation_date = resultSet.getString("reservation_date");

            System.out.println("| Reservation ID: " + id + " | Book ID: " + bookId
                    + " | Title: " + title + " | User: "
                    + user_name + " | Reservation Date: " + reservation_date + " |");

          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to list reservations.");
      System.out.println(e.getMessage());
    }
  }

  private void listAllFines() {
    String procedureCall = "{ CALL get_all_fines() }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            int id = resultSet.getInt("fine_id");
            int user_id = resultSet.getInt("user_id");
            String user_name = resultSet.getString("user_name");
            int amount = resultSet.getInt("amount");
            String status = resultSet.getString("paid");

            System.out.println("| Fine ID: " + id + " | User ID: " + user_id
                    + " | User: " + user_name + " | Amount: " + amount
                    + " | Date: " + status + " |");

          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to list fines.");
      System.out.println(e.getMessage());
    }
  }

  private void calculateFines(int index) {
    String procedureCall = "{ CALL calculate_fine(?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameter
        callableStatement.setInt(1, index);

        // Execute the stored procedure
        callableStatement.execute();
        double fine = callableStatement.getDouble(1);
        System.out.println("Fines calculated successfully!");
        System.out.println("Total fine of user with id " + index + " is: " + fine);
      }
    } catch (SQLException e) {
      System.out.println("Failed to calculate fines.");
      System.out.println(e.getMessage());
    }
  }

  private void givePrivileges(int index) {
    String procedureCall = "{ CALL grant_admin_role(?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameter
        callableStatement.setInt(1, index);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Admin privileges granted successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to grant admin privileges.");
      System.out.println(e.getMessage());
    }
  }

  private void removePrivileges(int index) {
    String procedureCall = "{ CALL revoke_admin_role(?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameter
        callableStatement.setInt(1, index);

        // Execute the stored procedure
        callableStatement.execute();
        System.out.println("Admin privileges revoked successfully!");
      }
    } catch (SQLException e) {
      System.out.println("Failed to revoke admin privileges.");
      System.out.println(e.getMessage());
    }
  }

  private void printAdminMainMenu() {
    System.out.println("--------------------");
    System.out.println("/\\ |) |\\/| | |\\|");
    System.out.println("--------------------");
    System.out.println("6. Add books");
    System.out.println("7. Add copies of books");
    System.out.println("8. Remove copies of books");
    System.out.println("bl. List ongoing book loans");
    System.out.println("rl. List all reservations");
    System.out.println("f. List all fines");
    System.out.println("c. Calculate fines from a certain user");
    System.out.println("gp. Give Privileges");
    System.out.println("rp. Remove Privileges");
  }

  private void printLoginMenu() {
    String email = "";
    String password = "";
    System.out.println("Please Login (l) or Register (r) to continue.");
    String input = scanner.nextLine();
    switch (input) {
      case "l":
        System.out.println("Enter your email:");
        email = scanner.nextLine();
        System.out.println("Enter your password:");
        password = scanner.nextLine();
        if (login(email, password)) {
          loggedIn = true;
          getPasswordInfoFromMail(email);
        } else {
          System.out.println("Invalid email or password. Please try again.");
        }
        break;
      case "r":
        System.out.println("Welcome in our system! \nPlease enter your name:");
        String name = scanner.nextLine();
        System.out.println("Hello! " + name + "\nPlease enter your email:");
        email = scanner.nextLine();
        System.out.println("Please enter your password:");
        password = scanner.nextLine();
        if (addUser(name, email, password)) {
          System.out.println("User added successfully!\nPlease login to continue.");
        } else {
          System.out.println("Failed to add user. Please try again.");
        }
        break;
      case "q":
        running = false;
        break;
      default:
        System.out.println("Invalid input. Please try again.");
    }
  }

  private boolean addUser(String name, String email, String password) {
    String procedureCall = "{ CALL addUser(?, ?, ?) }";
    password = PasswordUtils.hashPassword(password);
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameters
        callableStatement.setString(1, name);
        callableStatement.setString(2, email);
        callableStatement.setString(3, password);

        // Execute the stored procedure
        callableStatement.execute();
        return true;
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }

  }

  private void getPasswordInfoFromMail(String email) {
    String procedureCall = "{ CALL getUserByEmail(?) }";
    try {
      try (CallableStatement callableStatement = connection.prepareCall(procedureCall)) {
        // Set the input parameter
        callableStatement.setString(1, email);

        // Execute the stored procedure
        try (ResultSet resultSet = callableStatement.executeQuery()) {
          // Process the results
          while (resultSet.next()) {
            userId = resultSet.getInt("id");
            name = resultSet.getString("name");
            mail = resultSet.getString("email");
            role = resultSet.getString("role");

            System.out.println("ID: " + userId);
            System.out.println("Name: " + name);
            System.out.println("Email: " + mail);
            System.out.println("Role: " + role);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean login(String email, String password) {
    String functionCall = "{ ? = CALL get_user_password(?) }";

    try {
      try (CallableStatement callableStatement = connection.prepareCall(functionCall)) {
        // Register the output parameter
        callableStatement.registerOutParameter(1, java.sql.Types.VARCHAR);
        // Set the input parameter
        callableStatement.setString(2, email);
        // Execute the stored function
        callableStatement.execute();
        // Get the output parameter
        String hashedPassword = callableStatement.getString(1);
        if (hashedPassword == null) {
          return false;
        }
        return PasswordUtils.verifyPassword(password, hashedPassword);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return false;
    }

  }

//l




}
