import dao.RegistrationDAO;
import model.Registration;

public class TestRegistration {

    public static void main(String[] args) {

        // User ID 1 = Palak
        // Event ID 1 = Tech Conference
        Registration registration = new Registration(1, 1);

        RegistrationDAO registrationDAO = new RegistrationDAO();

        boolean success = registrationDAO.registerUser(registration);

        if (success) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("Registration failed.");
        }
    }
}
