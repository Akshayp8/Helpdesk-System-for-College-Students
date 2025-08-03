
package mypackage;
import java.util.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) {
        // Database connection
        String url = Data.url; // "jdbc:mysql://localhost:3306/your_database_name"
        String username = Data.username; // your username mostly it will be "root"
        String password = Data.password; // "your_password"
        Connection connection = null;

        try {
            // Establish connection
            connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println();
                System.out.println("Helpdesk System");
                System.out.println("1. Admin Login");
                System.out.println("2. Student Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                // Storing user input
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                    scanner.next(); // Consume invalid input
                    continue;
                }

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Admin login
                        if (Admin_login(connection, scanner)) {
                            System.out.println("Admin Login Successful");
                            // Show admin options
                            adminOptions(connection, scanner);
                        } else {
                            System.out.println("Admin Login Failed. Invalid credentials.");
                        }
                        break;

                    case 2:
                        // Student login
                        String studentGrNo = Student_login(connection, scanner);
                        if (studentGrNo != null) {
                            System.out.println("Student Login Successful");
                            // Show student options
                            studentOptions(connection, scanner, studentGrNo);
                        } else {
                            System.out.println("Student Login Failed. Invalid credentials.");
                        }
                        break;

                    case 3:
                        System.out.println("Exiting program...");
                        System.exit(0);

                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        } finally {
            // Close the connection resource
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the connection: " + e.getMessage());
                }
            }
        }
    }

    // Admin login method
    public static boolean Admin_login(Connection connection, Scanner scanner) throws SQLException {
        boolean adlog = false;  // Variable to store login success

        scanner.nextLine();  // Consume newline left from nextInt()
        System.out.print("Admin Username: ");
        String ad_username = scanner.nextLine();
        System.out.print("Admin Password: ");
        String ad_pass = scanner.nextLine();

        String login_query = "SELECT * FROM admin_login WHERE ad_username = ? AND ad_pass = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(login_query)) {
            preparedStatement.setString(1, ad_username);
            preparedStatement.setString(2, ad_pass);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    adlog = true;  // Login successful
                }
            }
        }

        return adlog;
    }

    // Method to show admin options after login
    public static void adminOptions(Connection connection, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println();
            System.out.println("Admin Menu");
            System.out.println("1. Student Details");
            System.out.println("2. Railway Concession Requests");
            System.out.println("3. Add New Student");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            // Validate integer input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.next(); // Consume invalid input
                continue;
            }

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // Handle displaying student details
                    displayStudentDetails(connection);
                    break;

                case 2:
                    // Handle displaying railway concession requests
                    displayRailwayConcessionRequests(connection,scanner);
                    break;

                case 3:
                    // Add Student details
                    addStudentDetails(connection,scanner);
                    break;


                case 4:
                    // Logout option - similar to Home in this context
                    System.out.println("Logging out...");
                    return;  // Exit adminOptions method to return to main menu

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Method to display student details
    public static void displayStudentDetails(Connection connection) throws SQLException {
        String query = "SELECT gr_no, sname, dob, course, branch, semester, address, category, fees, fees_status FROM Student_info";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("\n--- Student Details ---");
            System.out.printf("%-10s %-20s %-12s %-10s %-10s %-10s %-20s %-10s %-10s %-12s%n",
                    "GR No", "Name", "DOB", "Course", "Branch", "Semester", "Address", "Category", "Fees", "Fees Status");
            System.out.println("----------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                String grNo = resultSet.getString("gr_no");
                String name = resultSet.getString("sname");
                String dob = resultSet.getString("dob");
                String course = resultSet.getString("course");
                String branch = resultSet.getString("branch");
                int semester = resultSet.getInt("semester");
                String address = resultSet.getString("address");
                String category = resultSet.getString("category");
                int fees = resultSet.getInt("fees");
                String feesStatus = resultSet.getString("fees_status");

                System.out.printf("%-10s %-20s %-12s %-10s %-10s %-10d %-20s %-10s %-10d %-12s%n",
                        grNo, name, dob, course, branch, semester, address, category, fees, feesStatus);
            }
            System.out.println("----------------------------------------------------------------------------------------------------------");
        }
    }


    // Method to add a new student to the student_info table
    public static void addStudentDetails(Connection connection, Scanner scanner) throws SQLException {

        System.out.println("\n--- Add New Student Details ---");

        scanner.nextLine(); // Consume the leftover newline

        System.out.print("Enter GR Number: ");
        String grNo = scanner.nextLine();

        System.out.print("Enter Student Name: ");
        String sname = scanner.nextLine();

        System.out.print("Enter Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.nextLine();

        System.out.print("Enter Course: ");
        String course = scanner.nextLine();

        System.out.print("Enter Branch: ");
        String branch = scanner.nextLine();

        System.out.print("Enter Semester: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid semester (integer).");
            scanner.next();
            System.out.print("Enter Semester: ");
        }
        int semester = scanner.nextInt();

        scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        System.out.print("Enter Category (e.g., General, OBC, SC, etc.): ");
        String category = scanner.nextLine();

        // Input validation for Fees
        System.out.print("Enter Fees: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid fee amount (integer).");
            scanner.next(); // Consume invalid input
            System.out.print("Enter Fees: ");
        }
        int fees = scanner.nextInt();

        scanner.nextLine();  // Consume the leftover newline

        // Input Password for student
        System.out.print("Enter Password for Student: ");
        String password = scanner.nextLine();

        // Insert into the student_info table
        String insertQuery = "INSERT INTO Student_info (gr_no, sname, dob, course, branch, semester, address, category, fees, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, grNo);
            preparedStatement.setString(2, sname);
            preparedStatement.setString(3, dob);
            preparedStatement.setString(4, course);
            preparedStatement.setString(5, branch);
            preparedStatement.setInt(6, semester);
            preparedStatement.setString(7, address);
            preparedStatement.setString(8, category);
            preparedStatement.setInt(9, fees);
            preparedStatement.setString(10, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("New student added successfully!");
            } else {
                System.out.println("Failed to add the student. Please try again.");
            }
        }
    }


    // Method to display railway concession requests
    public static void displayRailwayConcessionRequests(Connection connection, Scanner scanner) throws SQLException {
       
        String query = "SELECT gr_no, `date`, sname, dob, age, period, classn, `from`, destination, status FROM rconcession_request WHERE status = 'Pending'";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("\n--- Pending Railway Concession Requests ---");
            System.out.printf("%-10s %-12s %-20s %-12s %-5s %-10s %-10s %-15s %-15s %-10s%n",
                    "GR No", "Date", "Name", "DOB", "Age", "Period", "Class", "From", "Destination", "Status");
            System.out.println("-----------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                String grNo = resultSet.getString("gr_no");
                String date = resultSet.getString("date");
                String name = resultSet.getString("sname");
                String dob = resultSet.getString("dob");
                int age = resultSet.getInt("age");
                String period = resultSet.getString("period");
                String classn = resultSet.getString("classn");
                String from = resultSet.getString("from");
                String destination = resultSet.getString("destination");
                String status = resultSet.getString("status");

                System.out.printf("%-10s %-12s %-20s %-12s %-5d %-10s %-10s %-15s %-15s %-10s%n",
                        grNo, date, name, dob, age, period, classn, from, destination, status);
            }
            System.out.println("-----------------------------------------------------------------------------------------------");

            System.out.print("Enter the GR No of the request to review (or 'exit' to go back): ");
            String selectedGrNo = scanner.next();

            if (selectedGrNo.equalsIgnoreCase("exit")) {
                return; // Exit the method if the user inputs "exit"
            }

            // Show the selected request's details
            String fetchQuery = "SELECT * FROM rconcession_request WHERE gr_no = ? AND status = 'Pending'";
            try (PreparedStatement fetchStatement = connection.prepareStatement(fetchQuery)) {
                fetchStatement.setString(1, selectedGrNo);

                try (ResultSet fetchResult = fetchStatement.executeQuery()) {
                    if (fetchResult.next()) {
                        // Display request details
                        System.out.println("\n--- Railway Concession Request Details ---");
                        System.out.println("GR No: " + fetchResult.getString("gr_no"));
                        System.out.println("Date: " + fetchResult.getString("date"));
                        System.out.println("Name: " + fetchResult.getString("sname"));
                        System.out.println("DOB: " + fetchResult.getString("dob"));
                        System.out.println("Age: " + fetchResult.getInt("age"));
                        System.out.println("Period: " + fetchResult.getString("period"));
                        System.out.println("Class: " + fetchResult.getString("classn"));
                        System.out.println("From: " + fetchResult.getString("from"));
                        System.out.println("Destination: " + fetchResult.getString("destination"));
                        System.out.println("------------------------------------------");

                        // Accept or Decline options
                        System.out.print("Enter 'accept' or 'decline' to process this request: ");
                        String decision = scanner.next();

                        // Validate the decision input
                        if (!decision.equalsIgnoreCase("accept") && !decision.equalsIgnoreCase("decline")) {
                            System.out.println("Invalid input. Please enter either 'accept' or 'decline'.");
                            return;
                        }

                        scanner.nextLine(); // Consume leftover newline
                        System.out.print("Enter any remarks (optional): ");
                        String remarks = scanner.nextLine();

                        // Update the request status in the database
                        String updateQuery = "UPDATE rconcession_request SET status = ?, remarks = ? WHERE gr_no = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, decision.equalsIgnoreCase("accept") ? "Accepted" : "Declined");
                            updateStatement.setString(2, remarks.isEmpty() ? null : remarks);
                            updateStatement.setString(3, selectedGrNo);

                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("Request " + (decision.equalsIgnoreCase("accept") ? "accepted" : "declined") + " successfully!");
                            } else {
                                System.out.println("Failed to update the request status.");
                            }
                        }
                    } else {
                        System.out.println("No pending request found for GR No: " + selectedGrNo);
                    }
                }
            }
        }
    }


    // Student login method (returns GR Number on success)
    public static String Student_login(Connection connection, Scanner scanner) throws SQLException {
        String gr_no = null;  // Variable to store GR Number on successful login

        scanner.nextLine();  // Consume newline left from nextInt()
        System.out.print("Student GR Number: ");
        gr_no = scanner.nextLine();
        System.out.print("Student Password: ");
        String sd_pass = scanner.nextLine();

        String login_query = "SELECT * FROM Student_info WHERE gr_no = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(login_query)) {
            preparedStatement.setString(1, gr_no);
            preparedStatement.setString(2, sd_pass);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return gr_no;  // Login successful, return GR Number
                }
            }
        }

        return null;  // Login failed
    }

    // Method to show student options after login
    public static void studentOptions(Connection connection, Scanner scanner, String gr_no) throws SQLException {
        while (true) {
            System.out.println();
            System.out.println("Student Menu");
            System.out.println("1. Railway Concession Form");
            System.out.println("2. Pay Fees");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");

            // Validate integer input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.next(); // Consume invalid input
                continue;
            }

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // Handle Railway Concession Form
                    railwayConcessionForm(connection, scanner, gr_no);
                    break;

                case 2:
                    //  Pay Fees function
                    payFees(connection, scanner, gr_no);
                    break;
                case 3:
                    // Logout option - similar to Home in this context
                    System.out.println("Logging out...");
                    return;  // Exit studentOptions method to return to main menu

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Method to handle Railway Concession Form
    public static void railwayConcessionForm(Connection connection, Scanner scanner, String gr_no) throws SQLException {
        // Fetch details from student_info
        String query = "SELECT gr_no, sname, dob FROM Student_info WHERE gr_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, gr_no);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("sname");
                    String dob = resultSet.getString("dob");

                    // Prompt the user for the remaining details
                    scanner.nextLine(); // Consume newline left from nextInt()
                    System.out.print("Enter Date (yyyy-mm-dd): "); // Date for request
                    String date = scanner.nextLine();

                    System.out.print("Enter Age: ");
                    while (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Please enter a valid age.");
                        scanner.next(); // Consume invalid input
                        System.out.print("Enter Age: ");
                    }
                    int age = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    System.out.print("Enter Period: ");
                    String period = scanner.nextLine();

                    System.out.print("Enter Class: ");
                    String classn = scanner.nextLine();

                    System.out.print("Enter From (Station): ");
                    String from = scanner.nextLine();

                    System.out.print("Enter Destination (Station): ");
                    String destination = scanner.nextLine();

                    // Display the form details
                    System.out.println("\n--- Railway Concession Form ---");
                    System.out.println("Date: " + date);
                    System.out.println("GR No: " + gr_no);
                    System.out.println("Name: " + name);
                    System.out.println("Age: " + age);
                    System.out.println("Date of Birth: " + dob);
                    System.out.println("Period: " + period);
                    System.out.println("Class: " + classn);
                    System.out.println("From: " + from);
                    System.out.println("Destination: " + destination);
                    System.out.println("-----------------------------");

                    // Insert into rconcession_request table with escaped reserved keywords
                    String insertQuery = "INSERT INTO rconcession_request (gr_no, `date`, sname, dob, age, period, classn, `from`, destination, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, gr_no);
                        insertStatement.setString(2, date); // Assuming `date` is VARCHAR
                        insertStatement.setString(3, name);
                        insertStatement.setString(4, dob);
                        insertStatement.setInt(5, age);
                        insertStatement.setString(6, period);
                        insertStatement.setString(7, classn);
                        insertStatement.setString(8, from);
                        insertStatement.setString(9, destination);
                        insertStatement.setString(10, "Pending");  // Setting the status to "Pending"

                        int rowsAffected = insertStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("Railway concession request submitted successfully!");
                        } else {
                            System.out.println("Failed to submit the railway concession request.");
                        }
                    }
                } else {
                    System.out.println("No student details found for GR Number: " + gr_no);
                }
            }
        }
    }


    // Method to handle Pay Fees option
    public static void payFees(Connection connection, Scanner scanner, String gr_no) throws SQLException {
        // Fetch details from student_info
        String query = "SELECT gr_no, sname, dob, course, branch, semester, address, category, fees, fees_status FROM student_info WHERE gr_no = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, gr_no);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Display student details
                    String name = resultSet.getString("sname");
                    String dob = resultSet.getString("dob");
                    String course = resultSet.getString("course");
                    String branch = resultSet.getString("branch");
                    int semester = resultSet.getInt("semester");
                    String address = resultSet.getString("address");
                    String category = resultSet.getString("category");
                    int fees = resultSet.getInt("fees");
                    String feesStatus = resultSet.getString("fees_status");

                    System.out.println("\n--- Student Fee Details ---");
                    System.out.println("GR No: " + gr_no);
                    System.out.println("Name: " + name);
                    System.out.println("Date of Birth: " + dob);
                    System.out.println("Course: " + course);
                    System.out.println("Branch: " + branch);
                    System.out.println("Semester: " + semester);
                    System.out.println("Address: " + address);
                    System.out.println("Category: " + category);
                    System.out.println("Fees: " + fees);
                    System.out.println("Fees Status: " + feesStatus);

                    // Check if fees are already paid
                    if ("paid".equalsIgnoreCase(feesStatus)) {
                        System.out.println("Fees have already been paid.");
                        return;
                    }

                    System.out.println("---------------------------");
                    System.out.print("Proceed to pay fees? (yes/no): ");
                    scanner.nextLine();  // Consume newline left from nextInt()
                    String response = scanner.nextLine();

                    if (response.equalsIgnoreCase("yes")) {
                        // Update payment status to 'paid'
                        String updateQuery = "UPDATE student_info SET fees_status = 'paid' WHERE gr_no = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setString(1, gr_no);
                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                System.out.println("Fees payment successful! Status updated to 'paid'.");
                            } else {
                                System.out.println("Failed to update fees status.");
                            }
                        }
                    } else {
                        System.out.println("Payment canceled.");
                    }
                } else {
                    System.out.println("No student details found for GR Number: " + gr_no);
                }
            }
        }
    }


}
