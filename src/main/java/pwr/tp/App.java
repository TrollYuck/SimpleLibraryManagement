package pwr.tp;

import java.sql.Connection;

public class App
{
  /**
   * Main method to run the application.
   *
   * @param args command line arguments
   */
  public static void main( String[] args )
  {
    try (Connection connection = DatabaseConnection.connect()) {
      if (connection != null) {
        System.out.println("Connected to the database!");
        Client client = new Client(connection);
        client.run();
        connection.endRequest();
      } else {
        System.out.println("Failed to make connection!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
