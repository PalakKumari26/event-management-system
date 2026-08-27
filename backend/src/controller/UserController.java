package controller;

import com.sun.net.httpserver.HttpExchange;
import dao.UserDAO;
import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class UserController {

    private final UserDAO userDAO = new UserDAO();

    public void register(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 204, "");
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = readRequestBody(exchange);

        String name = getValue(body, "name");
        String email = getValue(body, "email");
        String password = getValue(body, "password");

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            sendResponse(exchange, 400, "All fields are required.");
            return;
        }

        User user = new User(
                name,
                email,
                password,
                "ATTENDEE"
        );

        boolean success = userDAO.registerUser(user);

        if (success) {
            sendResponse(exchange, 200, "Registration successful!");
        } else {
            sendResponse(
                    exchange,
                    400,
                    "Registration failed. Email may already exist."
            );
        }
    }

    public void login(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 204, "");
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = readRequestBody(exchange);

        String email = getValue(body, "email");
        String password = getValue(body, "password");

        if (email.isEmpty() || password.isEmpty()) {
            sendResponse(
                    exchange,
                    400,
                    "Email and password are required."
            );
            return;
        }

        User user = userDAO.loginUser(email, password);

        if (user != null) {
            sendResponse(
                    exchange,
                    200,
                    "Login successful! Welcome " + user.getName()
            );
        } else {
            sendResponse(
                    exchange,
                    401,
                    "Invalid email or password."
            );
        }
    }

    public void getUser(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            sendResponse(exchange, 204, "");
            return;
        }

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("email=")) {
            sendResponse(exchange, 400, "Email is required.");
            return;
        }

        String email = query.substring(6);

        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            sendResponse(exchange, 404, "User not found.");
            return;
        }

        String json =
                "{"
                + "\"userId\":" + user.getUserId() + ","
                + "\"name\":\"" + user.getName() + "\","
                + "\"email\":\"" + user.getEmail() + "\","
                + "\"role\":\"" + user.getRole() + "\""
                + "}";

        sendResponse(exchange, 200, json);
    }

    private String readRequestBody(HttpExchange exchange)
            throws IOException {

        InputStream inputStream = exchange.getRequestBody();

        return new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    private String getValue(String body, String key) {

        String search = "\"" + key + "\":\"";

        int start = body.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();

        int end = body.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return body.substring(start, end);
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

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
            exchange.getResponseBody().write(responseBytes);
        }

        exchange.getResponseBody().close();
    }
}