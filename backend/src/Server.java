import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import controller.EventController;
import controller.RegistrationController;
import controller.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        UserController userController =
                new UserController();

        EventController eventController =
                new EventController();

        RegistrationController registrationController =
                new RegistrationController();

        // =========================
        // USER REGISTRATION
        // =========================

        server.createContext(
                "/api/register",
                userController::register
        );

        // =========================
        // LOGIN
        // =========================

        server.createContext(
                "/api/login",
                userController::login
        );

        // =========================
        // GET USER
        // =========================

        server.createContext(
                "/api/user",
                userController::getUser
        );

        // =========================
        // EVENTS
        // GET + POST
        // =========================

        server.createContext(
                "/api/events",
                eventController::handleEvents
        );

        // =========================
        // EVENT REGISTRATION
        // =========================

        server.createContext(
                "/api/event-register",
                registrationController::registerForEvent
        );

        // =========================
        // TEST
        // =========================

        server.createContext(
                "/api/test",
                Server::handleTest
        );

        server.setExecutor(null);

        server.start();

        System.out.println("=================================");
        System.out.println("Event Management Backend Started");
        System.out.println("Server: http://localhost:8080");
        System.out.println("=================================");
    }

    private static void handleTest(
            HttpExchange exchange)
            throws IOException {

        String response =
                "Event Management Backend is running!";

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "http://localhost:5173"
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain"
                );

        byte[] responseBytes =
                response.getBytes();

        exchange.sendResponseHeaders(
                200,
                responseBytes.length
        );

        exchange.getResponseBody()
                .write(responseBytes);

        exchange.getResponseBody().close();
    }
}