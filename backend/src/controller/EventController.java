package controller;

import com.sun.net.httpserver.HttpExchange;
import dao.EventDAO;
import model.Event;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EventController {

    private final EventDAO eventDAO = new EventDAO();

    public void handleEvents(HttpExchange exchange)
            throws IOException {

        String method = exchange.getRequestMethod();

        if (method.equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 204, "");
            return;
        }

        if (method.equalsIgnoreCase("GET")) {
            getEvents(exchange);
            return;
        }

        if (method.equalsIgnoreCase("POST")) {
            addEvent(exchange);
            return;
        }

        sendResponse(exchange, 405, "Method Not Allowed");
    }

    private void getEvents(HttpExchange exchange)
            throws IOException {

        List<Event> events = eventDAO.getAllEvents();

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < events.size(); i++) {

            Event event = events.get(i);

            json.append("{")
                    .append("\"eventId\":")
                    .append(event.getEventId())
                    .append(",")

                    .append("\"eventName\":\"")
                    .append(event.getEventName())
                    .append("\",")

                    .append("\"eventDate\":\"")
                    .append(event.getEventDate())
                    .append("\",")

                    .append("\"venue\":\"")
                    .append(event.getVenue())
                    .append("\",")

                    .append("\"capacity\":")
                    .append(event.getCapacity())
                    .append(",")

                    .append("\"availableSeats\":")
                    .append(event.getAvailableSeats())

                    .append("}");

            if (i < events.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendResponse(exchange, 200, json.toString());
    }

    private void addEvent(HttpExchange exchange)
            throws IOException {

        String body = readRequestBody(exchange);

        String eventName =
                getValue(body, "eventName");

        String eventDate =
                getValue(body, "eventDate");

        String venue =
                getValue(body, "venue");

        String capacityValue =
                getValue(body, "capacity");

        if (eventName.isEmpty()
                || eventDate.isEmpty()
                || venue.isEmpty()
                || capacityValue.isEmpty()) {

            sendResponse(
                    exchange,
                    400,
                    "All event fields are required."
            );

            return;
        }

        try {

            int capacity =
                    Integer.parseInt(capacityValue);

            if (capacity <= 0) {

                sendResponse(
                        exchange,
                        400,
                        "Capacity must be greater than 0."
                );

                return;
            }

            Event event = new Event(
                    eventName,
                    eventDate,
                    venue,
                    capacity,
                    capacity
            );

            boolean success =
                    eventDAO.addEvent(event);

            if (success) {

                sendResponse(
                        exchange,
                        200,
                        "Event added successfully!"
                );

            } else {

                sendResponse(
                        exchange,
                        400,
                        "Failed to add event."
                );
            }

        } catch (NumberFormatException e) {

            sendResponse(
                    exchange,
                    400,
                    "Capacity must be a valid number."
            );
        }
    }

    private String readRequestBody(
            HttpExchange exchange)
            throws IOException {

        InputStream inputStream =
                exchange.getRequestBody();

        return new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    private String getValue(
            String body,
            String key) {

        String search =
                "\"" + key + "\":\"";

        int start =
                body.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();

        int end =
                body.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return body.substring(start, end);
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response)
            throws IOException {

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "http://localhost:5173"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Methods",
                        "GET, POST, OPTIONS"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        if (responseBytes.length > 0) {
            exchange.getResponseBody()
                    .write(responseBytes);
        }

        exchange.getResponseBody().close();
    }
}