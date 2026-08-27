package dao;

import model.Registration;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegistrationDAO {

    public boolean registerUser(Registration registration) {

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();

            // Check whether the user has already registered
            String checkSql =
                    "SELECT * FROM registrations " +
                    "WHERE user_id = ? AND event_id = ?";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setInt(1, registration.getUserId());
            checkStatement.setInt(2, registration.getEventId());

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("User is already registered for this event.");
                return false;
            }

            // Check available seats
            String seatSql =
                    "SELECT available_seats FROM events WHERE event_id = ?";

            PreparedStatement seatStatement =
                    connection.prepareStatement(seatSql);

            seatStatement.setInt(1, registration.getEventId());

            ResultSet seatResult = seatStatement.executeQuery();

            if (!seatResult.next()) {
                System.out.println("Event not found.");
                return false;
            }

            int availableSeats = seatResult.getInt("available_seats");

            if (availableSeats <= 0) {
                System.out.println("No seats available.");
                return false;
            }

            // Register user
            String insertSql =
                    "INSERT INTO registrations (user_id, event_id) " +
                    "VALUES (?, ?)";

            PreparedStatement insertStatement =
                    connection.prepareStatement(insertSql);

            insertStatement.setInt(1, registration.getUserId());
            insertStatement.setInt(2, registration.getEventId());

            int rows = insertStatement.executeUpdate();

            if (rows > 0) {

                // Decrease available seats
                String updateSql =
                        "UPDATE events " +
                        "SET available_seats = available_seats - 1 " +
                        "WHERE event_id = ?";

                PreparedStatement updateStatement =
                        connection.prepareStatement(updateSql);

                updateStatement.setInt(1, registration.getEventId());
                updateStatement.executeUpdate();

                System.out.println("Registration successful!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}