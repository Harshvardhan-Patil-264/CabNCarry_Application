package com.example.cabn;

import android.content.Context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Generated;

public class DBConnection {
        // Database credentials and URL
        private static final String URL = "jdbc:mysql://192.168.25.171:3306/cabncarry?useSSL=false";
        private static final String USER = "root";
        private static final String PASSWORD = "Shiv@123#";

        // Static block to establish the connection when class is loaded
        public static Connection getConn() {
                try {
                        // Register MySQL JDBC driver (this line can be omitted in newer versions of Connector/J)
                        Class.forName("com.mysql.jdbc.Driver");

                        // Attempt to establish connection
                        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                        return connection;
                } catch (SQLException e) {
                        System.err.println("SQL Connection Error: " + e.getMessage());
                        e.printStackTrace();  // Prints full stack trace for better debugging
                        return null;
                } catch (Exception e) {
                        System.err.println("Driver Load Error: " + e.getMessage());
                        e.printStackTrace();  // Prints full stack trace for better debugging
                        return null;
                }
        }


        // send otp by checking the provided email is either user or driver
        // by email
        public static boolean sendOTP(String tbl, String email) {
                if (!isEmailRegistered(tbl, email)) {
                        System.out.println("Email not registered!");
                        return false; // Return false if email is not found
                }

                int generatedOTP = (int) (Math.random() * 9000) + 1000;  // Generate 4-digit OTP

                final boolean[] success = {false}; // Use an array to modify inside the thread

                Thread thread = new Thread(() -> {
                        boolean result = EmailSender.sendEMAIL(email, generatedOTP); // Replace with generated OTP
                        if (result) {
                                try (Connection conn = getConn()) {
                                        //store otp with email in otp_verification table

                                        if (!isEmailOTP(email)) {

                                                String sql = "INSERT INTO otp_verification (email, otp, expires_at) VALUES (?, ?, NOW() + INTERVAL 5 MINUTE)";
                                                PreparedStatement stmt = conn.prepareStatement(sql);
                                                stmt.setString(1, email);
                                                stmt.setInt(2, generatedOTP);
                                                stmt.executeUpdate();

                                                success[0] = true;
                                                System.out.println("OTP Sent Successfully!");
                                        } else {
                                                String sql = "UPDATE otp_verification SET otp = ?, expires_at = NOW() + INTERVAL 5 MINUTE WHERE email = ?;";
                                                PreparedStatement stmt = conn.prepareStatement(sql);
                                                stmt.setInt(1, generatedOTP); // Corrected: Set OTP first
                                                stmt.setString(2, email);      // Set email second
                                                stmt.executeUpdate();

                                                success[0] = true;
                                                System.out.println("OTP Sent Successfully!");
                                        }
                                } catch (SQLException e) {
                                        e.printStackTrace();
                                }
                        } else {
                                System.out.println("OTP Sending Failed!");
                        }
                });

                thread.start();

                try {
                        thread.join(); // Wait for thread to finish before returning result
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

                return success[0];
        }


        //verify the email otp
        // by email
        public static int checkLogin(String tbl, String email, int otp) {
                int userId = -1; // Default to -1 if login fails

                try (Connection conn = getConn()) {
                        String sql = "SELECT ud.id FROM " + tbl + " ud " + "JOIN otp_verification o ON ud.mail = o.email " + "WHERE o.email = ? AND o.otp = ? AND o.expires_at > NOW()";

                        //String sql1 = "SELECT COUNT(*) FROM otp_verification WHERE email = ? AND otp = ? AND expires_at > NOW()";

                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1, email);
                                stmt.setInt(2, otp);
                                try (ResultSet rs = stmt.executeQuery()) {
                                        if (rs.next()) {
                                                userId = rs.getInt("id"); // Get user ID from the result
                                        }
                                }
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }

                return userId; // Returns user ID if valid, or -1 if invalid
        }


        //get all information of user by id address to show profile
        public static User getUserById(String uord, int id) {
                User user = null;
                try (Connection conn = getConn()) {
                        String query = "SELECT * FROM " + uord + " WHERE id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setInt(1, id);
                        ResultSet rs = pstmt.executeQuery();

                        if (uord.equals("users")) {
                                if (rs.next()) {
                                        user = new User(
                                                rs.getString("username"),
                                                rs.getString("phone"),
                                                rs.getString("address"));
                                }
                        } else if (uord.equals("drivers")) {
                                if (rs.next()) {
                                        user = new User(
                                                rs.getString("name"),
                                                rs.getString("phone"),
                                                rs.getString("address"),
                                                rs.getString("license_number"),
                                                rs.getString("vehicle_type"),
                                                rs.getString("availability_status"),
                                                rs.getString("rating"));
                                }
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }
                return user;
        }


        //delete otp immediately from otpverification table after successfully user logged in
        // by email
        public static void deleteOTP(String email) {
                try (Connection conn = getConn()) {
                        String sql = "DELETE FROM otp_verification WHERE email = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, email);
                        int rowsDeleted = stmt.executeUpdate();

                        if (rowsDeleted > 0) {
                                System.out.println("OTP deleted successfully for " + email);
                        } else {
                                System.out.println("No OTP found to delete for " + email);
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error deleting OTP: " + e.getMessage());
                }
        }


        //check email is preent in db
        public static boolean isEmailRegistered(String tableName, String email) {
                boolean exists = false;

                // Ensure tableName is safe (no SQL injection risk)
                if (!tableName.equals("users") && !tableName.equals("drivers")) {
                        System.out.println("Invalid table name!");
                        return false;
                }

                String query = "SELECT COUNT(*) FROM " + tableName + " WHERE mail = ?"; // Dynamically set table name

                try (Connection conn = getConn();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                        if (conn == null) {
                                throw new SQLException("Database connection failed!");
                        }

                        stmt.setString(1, email);
                        try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next() && rs.getInt(1) > 0) {
                                        exists = true;
                                }
                        }

                } catch (SQLException e) {
                        e.printStackTrace();
                }

                return exists;
        }


        //
        public static boolean isEmailOTP(String email) {
                boolean exists = false;

                try (Connection conn = getConn();
                     PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM otp_verification WHERE email = ?")) {

                        if (conn == null) {
                                throw new SQLException("Database connection failed!");
                        }

                        stmt.setString(1, email);
                        try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next() && rs.getInt(1) > 0) {
                                        exists = true;
                                }
                        }

                } catch (SQLException e) {
                        e.printStackTrace();
                }

                return exists;
        }


        public static boolean isPhoneRegistered(String phone) {
                boolean exists = false;

                try (Connection conn = getConn();
                     PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE phone = ?")) {

                        if (conn == null) {
                                throw new SQLException("Database connection failed!");
                        }

                        stmt.setString(1, phone);
                        try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next() && rs.getInt(1) > 0) {
                                        exists = true;
                                }
                        }

                } catch (SQLException e) {
                        e.printStackTrace();
                }
                return exists;
        }


        //register new user
        public static boolean RegisterUser(String username, String phone, String mail, String addr) {
                boolean reg = false;

                try (Connection conn = getConn()) {
                        if (conn == null) {
                                System.out.println("Database connection failed!");
                                return false;
                        }

                        // Check if email or phone already exists
                        if (isEmailRegistered("users", mail) || isPhoneRegistered(username)) {
                                System.out.println("Email or Phone already registered!");
                                return false;
                        }

                        // Proceed with registration
                        String sql = "INSERT INTO users (username, phone, mail, address, availability) VALUES (?, ?, ?, ?, 'avl')";
                        PreparedStatement stmt = conn.prepareStatement(sql);

                        stmt.setString(1, username);
                        stmt.setString(2, phone);
                        stmt.setString(3, mail);
                        stmt.setString(4, addr);

                        reg = stmt.executeUpdate() > 0;

                        if (reg) {
                                System.out.println("User registered successfully!");
                        } else {
                                System.out.println("User registration failed!");
                        }

                } catch (SQLException e) {
                        e.printStackTrace();
                }

                return reg;
        }


        //Rides file for store the data
        public static List<Rides> getUserRide(Context context, String uord, int id) {
                List<Rides> ridelists = new ArrayList<>();

                try (Connection conn = getConn()) {

                        if (conn == null) {
                                System.out.println("Connection Faliled...");
                                return ridelists;
                        }
                        String query = null;

                        if (uord.equals("users")) {
                                query = "SELECT * FROM userride WHERE userid = ?";
                        } else if (uord.equals("drivers")) {
                                query = "SELECT * FROM userride WHERE driverid = ?";
                        }

                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                                pstmt.setInt(1, id);
                                ResultSet rs = pstmt.executeQuery();

                                while (rs.next()) {
                                        Rides rd = new Rides(
                                                context,
                                                rs.getInt("rideid"),
                                                rs.getInt("userid"),
                                                rs.getInt("driverid"),
                                                rs.getString("userphone"),
                                                rs.getString("startPlace"),
                                                rs.getString("endPlace"),
                                                rs.getString("bookeddaytime"),
                                                rs.getString("status"),
                                                rs.getString("cartype"),
                                                rs.getString("totalprice"),
                                                rs.getInt("ridecode"),
                                                rs.getString("pay_mode")
                                        );

                                        ridelists.add(rd);
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return ridelists;
        }


        public static List<RideRequestsDriver> getRequests(Context context, String vhcl) {
                List<RideRequestsDriver> rideRequestsDrivers = new ArrayList<>();

                try (Connection conn = getConn()) {
                        if (conn == null) {
                                return rideRequestsDrivers;
                        }

                        String gett = "SELECT rr.* FROM riderequest rr JOIN userride ur ON rr.acceptidof = ur.rideid WHERE rr.status = 'pending' AND ur.status = 'pending' AND rr.cartype = ? ";

                        try (PreparedStatement preparedStatement = conn.prepareStatement(gett)) {
                                preparedStatement.setString(1, vhcl);
                                ResultSet rs = preparedStatement.executeQuery();

                                while (rs.next()) {
                                        RideRequestsDriver driver = new RideRequestsDriver(
                                                context,
                                                rs.getInt("rideid"),
                                                rs.getInt("userid"),
                                                rs.getInt("acceptidof"),
                                                rs.getString("startPlace"),
                                                rs.getString("endPlace"),
                                                rs.getString("cartype"),
                                                rs.getFloat("totalprice"),
                                                rs.getInt("ridecode")
                                        );
                                        rideRequestsDrivers.add(driver);
                                }
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }

                return rideRequestsDrivers;
        }


        public static boolean AcceptRide(int rideId, int driverId, int acptId) {
                boolean acpt = false;

                try (Connection connection = getConn()) {
                        if (connection == null) {
                                return false;
                        }

                        // Fetch driver details
                        User driver = getUserById("drivers", driverId);
                        if (driver == null || !"avl".equals(driver.getAvilability())) {
                                return false; // Driver is not available
                        }

                        connection.setAutoCommit(false);

                        String query = "UPDATE userride SET driverid = ?, status = 'accept' WHERE rideid = ?";
                        String deleteRequestQuery = "DELETE FROM riderequest WHERE rideid = ?";
                        String updateAvailabilityQuery = "UPDATE drivers SET availability_status = 'onride' WHERE id = ?";

                        try (PreparedStatement rideStmt = connection.prepareStatement(query);
                             PreparedStatement deleteRequestStmt = connection.prepareStatement(deleteRequestQuery);
                             PreparedStatement updateDriverStmt = connection.prepareStatement(updateAvailabilityQuery)) {

                                // Update userride table
                                rideStmt.setInt(1, driverId);
                                rideStmt.setInt(2, acptId);
                                int rideUpdated = rideStmt.executeUpdate();

                                if (rideUpdated > 0) {
                                        // Delete from riderequest table
                                        deleteRequestStmt.setInt(1, rideId);
                                        int requestDeleted = deleteRequestStmt.executeUpdate();

                                        if (requestDeleted > 0) {
                                                // Update driver availability
                                                updateDriverStmt.setInt(1, driverId);
                                                int driverUpdated = updateDriverStmt.executeUpdate();

                                                if (driverUpdated > 0) {
                                                        acpt = true;
                                                        connection.commit(); // Commit transaction
                                                }
                                        }
                                }
                        } catch (SQLException e) {
                                connection.rollback(); // Rollback on failure
                                throw new RuntimeException(e);
                        } finally {
                                connection.setAutoCommit(true);
                        }

                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }

                return acpt;
        }


        //add new user ride request
        public static String addUserRequest(int idd, String ph, String from, String to, String vehicle, float totalPrice, String mode) {
                String result = "failed";

                try (Connection connection = getConn()) {
                        if (connection == null) {
                                System.out.println("Connection Failed");
                                return "not";
                        }

                        String check_avl = "SELECT availability FROM users WHERE id = ?";

                        try (PreparedStatement p1 = connection.prepareStatement(check_avl)) {
                                p1.setInt(1, idd);
                                try (ResultSet rs1 = p1.executeQuery()) {
                                        if (rs1.next()) {
                                                String strr = rs1.getString("availability");

                                                if (strr.equalsIgnoreCase("avl")) {

                                                        connection.setAutoCommit(false); // Start transaction

                                                        String addRequestUserRide = "INSERT INTO userride (userid, driverid, userphone, startPlace, endPlace, bookeddaytime, status, cartype, totalprice, ridecode, pay_mode) VALUES (?, NULL, ?, ?, ?, NOW(), 'pending', ?, ?, ?, ?)";
                                                        String addRequestUserRideDriver = "INSERT INTO riderequest (userid, acceptidof, userphone, startPlace, endPlace, bookeddaytime, status, cartype, totalprice, ridecode, pay_mode) VALUES (?, ?, ?, ?, ?, NOW(), 'pending', ?, ?, ?, ?)";

                                                        try (PreparedStatement stmt = connection.prepareStatement(addRequestUserRide, Statement.RETURN_GENERATED_KEYS);
                                                             PreparedStatement stmt1 = connection.prepareStatement(addRequestUserRideDriver)) {

                                                                int generatedOTP = (int) (Math.random() * 9000) + 1000;  // Generate 4-digit OTP

                                                                // Set values for userride table
                                                                stmt.setInt(1, idd);
                                                                stmt.setString(2, ph);
                                                                stmt.setString(3, from);
                                                                stmt.setString(4, to);
                                                                stmt.setString(5, vehicle);
                                                                stmt.setFloat(6, totalPrice);
                                                                stmt.setInt(7, generatedOTP);
                                                                stmt.setString(8, mode);

                                                                int rows1 = stmt.executeUpdate();

                                                                if (rows1 > 0) {
                                                                        ResultSet keyy = stmt.getGeneratedKeys();
                                                                        if (keyy.next()) {
                                                                                int rideId = keyy.getInt(1);

                                                                                // Set values for riderequest table
                                                                                stmt1.setInt(1, idd);
                                                                                stmt1.setInt(2, rideId);
                                                                                stmt1.setString(3, ph);
                                                                                stmt1.setString(4, from);
                                                                                stmt1.setString(5, to);
                                                                                stmt1.setString(6, vehicle);
                                                                                stmt1.setFloat(7, totalPrice);
                                                                                stmt1.setInt(8, generatedOTP);
                                                                                stmt1.setString(9, mode);

                                                                                int rows2 = stmt1.executeUpdate();

                                                                                if (rows2 > 0) {

                                                                                        String qr1 = "UPDATE users SET availability = ? WHERE id = ?";
                                                                                        try (PreparedStatement pt1 = connection.prepareStatement(qr1)) {
                                                                                                pt1.setString(1, "onride");
                                                                                                pt1.setInt(2, idd);

                                                                                                int pt1_check = pt1.executeUpdate();
                                                                                                if (pt1_check > 0) {
                                                                                                        connection.commit();
                                                                                                        result = "success";
                                                                                                        return result;
                                                                                                } else {
                                                                                                        connection.rollback();
                                                                                                        result = "failed";
                                                                                                        return result;
                                                                                                }

                                                                                        }
                                                                                } else {
                                                                                        connection.rollback(); // Rollback if riderequest fails
                                                                                        result = "failed";
                                                                                        return result;
                                                                                }
                                                                        }
                                                                } else {
                                                                        connection.rollback(); // Rollback if userride fails
                                                                        result = "failed";
                                                                        return result;
                                                                }

                                                        } catch (SQLException e) {
                                                                connection.rollback(); //rollback due error
                                                                e.printStackTrace();
                                                                result = "error";
                                                                return result;
                                                        } finally {
                                                                connection.setAutoCommit(true);
                                                        }
                                                } else {
                                                        return "failed";
                                                }
                                        }
                                }
                        } catch (SQLException e) {
                                throw new RuntimeException(e);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
                return result;
        }




        //by user
        public static boolean CancleRide(int ride) {
                try (Connection connection = getConn()) {
                        if (connection == null) {
                                return false;
                        }

                        connection.setAutoCommit(false); // Begin transaction

                        String query = "UPDATE userride SET status = 'cancelled' WHERE rideid = ?";
                        String query1 = "SELECT userid FROM userride WHERE rideid = ?";
                        String query2 = "UPDATE users SET availability = 'avl' WHERE id = ?";

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                preparedStatement.setInt(1, ride);
                                int row = preparedStatement.executeUpdate();

                                if (row > 0) {
                                        try (PreparedStatement preparedStatement1 = connection.prepareStatement(query1)) {
                                                preparedStatement1.setInt(1, ride);
                                                try (ResultSet rs1 = preparedStatement1.executeQuery()) {
                                                        if (rs1.next()) {
                                                                int iddd = rs1.getInt("userid");

                                                                if (iddd > 0) {
                                                                        try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                                                                                preparedStatement2.setInt(1, iddd);
                                                                                int done = preparedStatement2.executeUpdate();

                                                                                if (done > 0) {
                                                                                        connection.commit(); // Commit if everything succeeds
                                                                                        return true;
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }

                                connection.rollback(); // Rollback if any step fails
                                return false;

                        } catch (SQLException e) {
                                connection.rollback(); // Rollback on exception
                                throw new RuntimeException(e);
                        } finally {
                                connection.setAutoCommit(true); // Reset auto-commit mode
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
        }


        //by driver
        public static boolean CompleteRide(int ride, int myid) {
                boolean cncl = false;

                try (Connection connection = getConn()) {
                        if (connection == null) {
                                return false;
                        }

                        connection.setAutoCommit(false);
                        String query = "UPDATE userride SET status = 'complete' WHERE rideid = ?";
                        String query2 = "UPDATE drivers SET availability_status = 'avl' WHERE id = ?";
                        String query3 = "UPDATE users SET availability = 'avl' WHERE id = ?";
                        String query4 = "SELECT userid FROM userride WHERE rideid = ?";

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                             PreparedStatement preparedStatement1 = connection.prepareStatement(query2);
                             PreparedStatement preparedStatement2 = connection.prepareStatement(query3);
                             PreparedStatement preparedStatement3 = connection.prepareStatement(query4)) {

                                preparedStatement.setInt(1, ride);
                                int row = preparedStatement.executeUpdate();

                                if (row > 0) {
                                        preparedStatement1.setInt(1, myid);
                                        int row1 = preparedStatement1.executeUpdate();

                                        if (row1 > 0) {
                                                preparedStatement3.setInt(1, ride);
                                                try (ResultSet rs1 = preparedStatement3.executeQuery()) {
                                                        if (rs1.next()) {
                                                                int id1 = rs1.getInt("userid");

                                                                if (id1 > 0) {
                                                                        preparedStatement2.setInt(1, id1);
                                                                        int done = preparedStatement2.executeUpdate();

                                                                        if (done > 0) {
                                                                                connection.commit(); // Commit if everything succeeds
                                                                                cncl = true;
                                                                        } else {
                                                                                connection.rollback();
                                                                        }
                                                                } else {
                                                                      connection.rollback();
                                                                }
                                                        }
                                                }
                                        } else {
                                                connection.rollback();
                                        }
                                } else {
                                        connection.rollback();
                                }
                        } catch (SQLException e) {
                                connection.rollback(); // Explicit rollback on exception
                                throw new RuntimeException(e);
                        } finally {
                                connection.setAutoCommit(true);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(e);
                }

                return cncl;
        }


        //by driver
        public static boolean StartRide(int ride, int myid) {
                boolean started = false;

                try (Connection connection = getConn()) {
                        if (connection == null) {
                                return false;
                        }

                        // Ensure the ride belongs to the driver and is not already completed/cancelled
                        String query = "UPDATE userride SET status = 'ongoing' WHERE rideid = ? AND driverid = ? AND status NOT IN ('complete', 'cancelled')";

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                                preparedStatement.setInt(1, ride);
                                preparedStatement.setInt(2, myid);

                                int row = preparedStatement.executeUpdate();
                                started = row > 0; // Returns true if the update was successful
                        }
                } catch (SQLException e) {
                        System.err.println("SQL Error in StartRide: " + e.getMessage());
                }

                return started;
        }

}
