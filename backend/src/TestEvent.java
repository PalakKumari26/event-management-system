import dao.EventDAO;
import model.Event;

public class TestEvent {

    public static void main(String[] args) {

        Event event = new Event(
                "Tech Conference",
                "2026-09-15",
                "Bangalore",
                100,
                100
        );

        EventDAO eventDAO = new EventDAO();

        boolean success = eventDAO.addEvent(event);

        if (success) {
            System.out.println("Event added successfully!");
        } else {
            System.out.println("Failed to add event.");
        }

        System.out.println("\nAll Events:");

        for (Event e : eventDAO.getAllEvents()) {

            System.out.println(
                    e.getEventId() + " | " +
                    e.getEventName() + " | " +
                    e.getEventDate() + " | " +
                    e.getVenue() + " | Capacity: " +
                    e.getCapacity() + " | Available: " +
                    e.getAvailableSeats()
            );
        }
    }
}