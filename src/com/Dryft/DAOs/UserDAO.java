package com.Dryft.DAOs;

import com.Dryft.exceptions.UserSideException;
import com.Dryft.models.User;
import com.Dryft.utils.DBConn;
import com.Dryft.utils.Hasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static void createUser(User user) throws SQLException {
        Connection conn = DBConn.getConn();
        checkUserAlreadyExists(user, conn);
        String[] passCredentials = Hasher.hashPassword(user.getPassword());
        String query = "Insert into users values (?,?,?,?,?,?);";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, user.getEmail());
        st.setString(2, user.getFullname());
        st.setString(3, passCredentials[0]);
        st.setString(4, passCredentials[1]);
        st.setString(5, String.valueOf(user.getSex()));
        st.setInt(6, user.getBalance());
        st.executeQuery();
        DBConn.closeConn();
    }

    private static void checkUserAlreadyExists(User user, Connection conn) throws SQLException {
        String query = "Select email from users where email = (?);";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, user.getEmail());
        ResultSet result = st.executeQuery();
        if (result.next())
            throw new UserSideException(UserSideException.ErrorCode.EmailAlreadyExists);
    }

    private static void validateCredentials(String email, String password, Connection conn) throws SQLException {
        String query = "Select password, salt from users where email = (?);";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, email);
        ResultSet result = st.executeQuery();
        if (result.next()) {
            String hashedPassword = Hasher.hashPasswordWithSalt(password, result.getString("salt"));
            if (!(result.getString("email").equals(email) && result.getString("password").equals(hashedPassword)))
                throw new UserSideException(UserSideException.ErrorCode.InvalidCredentials);
        } else
            throw new UserSideException(UserSideException.ErrorCode.UserNotFound);
    }

    public static User retrieveUserDetails(String email, String password) throws SQLException {
        Connection conn = DBConn.getConn();
        validateCredentials(email, password, conn);
        String query = "Select fullname, sex, balance from users where email = (?);";
        PreparedStatement st = conn.prepareStatement(query);
        st.setString(1, email);
        ResultSet result = st.executeQuery();
        result.next();
        String fullname = result.getString("fullname");
        char sex = result.getString("sex").charAt(0);
        int balance = result.getInt("balance");
        User user = new User(fullname, email, password, sex, balance);
        DBConn.closeConn();
        return user;
    }

    public static boolean addMoney(User user, int amt) throws SQLException {
        Connection conn = DBConn.getConn();
        var st = conn.prepareStatement("SELECT balance FROM users where email = (?);");
        st.setString(1, user.getEmail());
        var result = st.executeQuery();
        if (result.next()) {
            var initBalance = result.getInt("balance");
            if (initBalance + amt >= 0) {
                st = conn.prepareStatement("UPDATE users SET balance = (?) WHERE email = (?);");
                st.setInt(1, initBalance + amt);
                st.setString(2, user.getEmail());
                st.executeUpdate();
                DBConn.closeConn();
                user.setBalance(initBalance + amt);
                return true;
            } else {
                DBConn.closeConn();
                return false;
            }
        } else {
            DBConn.closeConn();
            throw new IllegalArgumentException("User not found");
        }
    }


}
