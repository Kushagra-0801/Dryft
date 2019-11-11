package com.Dryft.DAOs;

import com.Dryft.models.Car;
import com.Dryft.models.Driver;
import com.Dryft.models.Location;
import com.Dryft.utils.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {
    public static Driver getDriver(int id) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM drivers WHERE id = (?);");
        st.setInt(1, id);
        ResultSet result = st.executeQuery();
        DBConn.closeConn();
        if (result.next()) {
            return new Driver(
                    result.getInt("id"),
                    result.getString("name"),
                    CarDAO.getCar(result.getString("CarNumber")),
                    LocationDAO.getLocation(result.getString("location")),
                    result.getString("sex").charAt(0),
                    result.getDouble("rating"),
                    result.getInt("reviews"),
                    result.getBoolean("onRoad")
            );
        } else {
            throw new IllegalArgumentException("Driver not found");
        }
    }

    public static List<Driver> getAllDriversInALocation(Location location) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM drivers where location = (?);");
        ResultSet result = st.executeQuery();
        while (result.next()) {
            Driver driver = new Driver(
                    result.getInt("id"),
                    result.getString("name"),
                    CarDAO.getCar(result.getString("CarNumber")),
                    LocationDAO.getLocation(result.getString("location")),
                    result.getString("sex").charAt(0),
                    result.getDouble("rating"),
                    result.getInt("reviews"),
                    result.getBoolean("onRoad")
            );
            drivers.add(driver);
        }
        return drivers;
    }

    public static Driver getBestDriver(char userSex, Location origin, Car.CarType type) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement(
                "SELECT * FROM " +
                        "(drivers INNER JOIN cars c ON drivers.carNumber = c.licenseNumber INNER JOIN locations l on drivers.location = l.name) " +
                        "WHERE sex = (?) AND CarType = (?) " +
                        "ORDER BY (abs((?) - x) + abs((?) - y)), rating DESC;"
        );
        st.setString(1, String.valueOf(userSex));
        st.setString(2, type.name());
        st.setInt(3, origin.getX());
        st.setInt(4, origin.getY());
        var result = st.executeQuery();
        DBConn.closeConn();
        if (result.next()) {
            return new Driver(
                    result.getInt("id"),
                    result.getString("name"),
                    CarDAO.getCar(result.getString("CarNumber")),
                    LocationDAO.getLocation(result.getString("location")),
                    result.getString("sex").charAt(0),
                    result.getDouble("rating"),
                    result.getInt("reviews"),
                    result.getBoolean("onRoad")
            );
        } else {
            throw new IllegalArgumentException("Driver not found");
        }
    }

    public static boolean markDriverOnRoadIfFree(int id) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT onRoad from drivers where id = (?);");
        st.setInt(1, id);
        var result = st.executeQuery();
        if (result.next()) {
            if (!result.getBoolean("onRoad")) {
                st = conn.prepareStatement("UPDATE drivers set onRoad = (?) WHERE id = (?);");
                st.setBoolean(1, true);
                st.setInt(2, id);
                st.executeQuery();
                return true;
            } else {
                return false;
            }
        } else {
            throw new IllegalArgumentException("Driver not found");
        }
    }
}
