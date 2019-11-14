package com.Dryft.DAOs;

import com.Dryft.models.Car;
import com.Dryft.models.Driver;
import com.Dryft.models.Location;
import com.Dryft.utils.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DriverDAO {

    public static Driver getDriver(int id) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM drivers WHERE id = (?);");
        st.setInt(1, id);
        ResultSet result = st.executeQuery();
        DBConn.closeConn();
        if (result.next()) {
            return new Driver(result.getInt("id"), result.getString("name"),
                    CarDAO.getCar(result.getString("CarNumber")), LocationDAO.getLocation(result.getString("location")),
                    result.getString("sex").charAt(0), result.getDouble("rating"), result.getInt("reviews"),
                    result.getBoolean("onRoad"));
        } else {
            throw new IllegalArgumentException("Driver not found");
        }
    }
 
    public static Driver getBestDriver(Location origin, Car.CarType type) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM "
                + "(drivers INNER JOIN cars c ON drivers.carNumber = c.licenseNumber INNER JOIN locations l on drivers.location = l.name) "
                + "WHERE CarType = (?) AND onRoad = (?) "
                + "ORDER BY (abs((?) - x) + abs((?) - y)), rating DESC;"
        );
        st.setString(1, type.name());
        st.setBoolean(2, false);
        st.setInt(3, origin.getX());
        st.setInt(4, origin.getY());
        var result = st.executeQuery();
        if (result.next()) {
            Driver d = new Driver(
                    result.getInt("id"),
                    result.getString("name"),
                    CarDAO.getCar(result.getString("CarNumber")),
                    LocationDAO.getLocation(result.getString("location")),
                    result.getString("sex").charAt(0),
                    result.getDouble("rating"),
                    result.getInt("reviews"),
                    result.getBoolean("onRoad")
            );
            DBConn.closeConn();
            return d;
        } else {
            DBConn.closeConn();
            throw new IllegalArgumentException("Driver not found");
        }
    }

    public static void checkDriverAvailibility(Driver driver) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("Select onRoad from drivers where id = (?);");
        st.setInt(1, driver.getId());
        ResultSet result = st.executeQuery();
        if (result.next()) {
            if (result.getBoolean("onRoad")) {
                DBConn.closeConn();
                throw new IllegalArgumentException("Driver Already Occupied");
            }
        } else {
            DBConn.closeConn();
            throw new IllegalArgumentException("Driver not found");
        }
        DBConn.closeConn();
    }

    public static void freeDriver(int id) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("Update drivers set onRoad = (?);");
        st.setBoolean(1, false);
        ResultSet result = st.executeQuery();
        if (!result.next()) {
            DBConn.closeConn();
            throw new IllegalArgumentException("Invalid Driver");
        }
        DBConn.closeConn();
    }
}
