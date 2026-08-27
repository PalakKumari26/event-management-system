import dao.UserDAO;
import model.User;

public class TestUser {

    public static void main(String[] args) {

        User user = new User(
                "Palak",
                "palak@example.com",
                "test123",
                "ATTENDEE"
        );

        UserDAO userDAO = new UserDAO();

        boolean result = userDAO.registerUser(user);

        if (result) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("User registration failed!");
        }
    }
}
