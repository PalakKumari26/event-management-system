package controller;

import com.sun.net.httpserver.HttpExchange;
import dao.RegistrationDAO;
import model.Registration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RegistrationController {

    private final RegistrationDAO registrationDAO =
            new RegistrationDAO();

    public void registerForEvent(HttpExchange exchange)
            throws IOException {

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            sendResponse(exchange, 204, "");
            return;
        }

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "Method Not Allowed"
            );
            return;
        }

        String body = readRequestBody(exchange);

        String userIdValue =
                getValue(body, "userId");

        String eventIdValue =
                getValue(body, "eventId");

        if (userIdValue.isEmpty()
                || eventIdValue.isEmpty()) {

            sendResponse(
                    exchange,
                    400,
                    "User ID and Event ID are required."
            );
            return;
        }

        try {

            int userId =
                    Integer.parseInt(userIdValue);

            int eventId =
                    Integer.parseInt(eventIdValue);

            Registration registration =
                    new Registration(userId, eventId);

            boolean success =
                    registrationDAO.registerUser(
                            registration
                    );

            if (success) {

                sendResponse(
                        exchange,
                        200,
                        "Event registration successful!"
                );

            } else {

                sendResponse(
                        exchange,
                        400,
                        "Registration failed. You may already be registered or no seats are available."
                );
            }

        } catch (NumberFormatException e) {

            sendResponse(
                    exchange,
                    400,
                    "Invalid user ID or event ID."
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
                "\"" + key + "\":";

        int start =
                body.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();

        while (start < body.length()
                && Character.isWhitespace(
                        body.charAt(start))) {

            start++;
        }

        int end = start;

        while (end < body.length()
                && (Character.isDigit(
                        body.charAt(end))
                || body.charAt(end) == '-')) {

            end++;
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
                        "text/plain"
                );

        byte[] responseBytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

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