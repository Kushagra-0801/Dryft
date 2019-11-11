package com.Dryft.DAOs;

import java.sql.*;
import com.Dryft.models.User;
import com.Dryft.utils.DBConn;
import com.Dryft.utils.Hasher;
import com.Dryft.exceptions.UserSideException;

public class UserDAO {

    public static void createUser(User user) throws SQLException {
        Connection conn = DBConn.getConn();
        checkUserAlreadyExists(user, conn);
        String passCredentials[] = Hasher.hashPassword(user.getPassword());
        String query = "Insert into users values (?,?,?,?,?,?)";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(6, user.getEmail(), user.getFullname(), passCredentials[0], passCredentials[1], user.getSex(),
                user.getBalance());
        ResultSet result = st.executeQuery();
        DBConn.closeConn();
    }

    private static void checkUserAlreadyExists(User user, Connection conn) throws SQLException {
        String query = "Select email from users where email = (?)";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, user.getEmail());
        ResultSet result = st.executeQuery();
        if (result.next())
            throw new UserSideException(UserSideException.ErrorCode.EmailAlreadyExists);
    }

    private static void validateCredentials(String email, String password, Connection conn) throws SQLException {
        String query = "Select password,salt from users where email = (?)";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, user.getEmail());
        ResultSet result = st.executeQuery();
        if (result.next()) {
            String hashedPassword = Hasher.hashPasswordWithSalt(password, result.getString("salt"));
            if (!(result.getString("email") == email && result.getString("password") == hashedPassword))
                throw new UserSideException(UserSideException.ErrorCode.InvalidCredentials);
        } else
            throw new UserSideException(UserSideException.ErrorCode.UserNotFound);

    }

    public static User retrieveUserDetails(String email, String password) throws SQLException {
        Connection conn = DBConn.getConn();
        validateCredentials(email, password, conn);
        String query = "Select fullname,sex,balance from users where email = (?)";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, user.getEmail());
        ResultSet result = st.executeQuery();
        result.next();
        String fullname = result.getString("fullname");
        char sex = result.getString("sex").charAt(0);
        int balance = result.getInt("balance");
        User user = new User(fullname, email, password, sex, balance);
        DBConn.closeConn();
        return user;
    }

    public static void incrementBalance(String email, int balance) throws SQLException {
        Connection conn = DBConn.getConn();
        String query = "Update users set balance= (?) where email = (?);";
        PreparedStatement st = conn.prepareStatement(query);
        st.setInt(1, balance);
        st.setString(2, email);
        ResultSet result = st.executeQuery();
        if (!result.next())
            throw new IllegalArgumentException("User not found");
        DBConn.closeConn();
    }
}
