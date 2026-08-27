package dao;

import model.Event;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    public boolean addEvent(Event event) {

        String sql = "INSERT INTO events " +
                     "(event_name, event_date, venue, capacity, available_seats) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getEventName());
            statement.setString(2, event.getEventDate());
            statement.setString(3, event.getVenue());
            statement.setInt(4, event.getCapacity());
            statement.setInt(5, event.getAvailableSeats());

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getAllEvents() {

        List<Event> events = new ArrayList<>();

        String sql = "SELECT * FROM events";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Event event = new Event(
                    resultSet.getInt("event_id"),
                    resultSet.getString("event_name"),
                    resultSet.getString("event_date"),
                    resultSet.getString("venue"),
                    resultSet.getInt("capacity"),
                    resultSet.getInt("available_seats")
                );

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }
    public boolean updateEvent(Event event) {

    String sql = "UPDATE events SET event_name = ?, event_date = ?, " +
                 "venue = ?, capacity = ?, available_seats = ? " +
                 "WHERE event_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, event.getEventName());
        statement.setString(2, event.getEventDate());
        statement.setString(3, event.getVenue());
        statement.setInt(4, event.getCapacity());
        statement.setInt(5, event.getAvailableSeats());
        statement.setInt(6, event.getEventId());

        return statement.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


public boolean deleteEvent(int eventId) {

    String sql = "DELETE FROM events WHERE event_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, eventId);

        return statement.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}