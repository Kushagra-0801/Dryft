package com.Dryft.DAOs;

import com.Dryft.models.Ride;
import com.Dryft.models.User;
import com.Dryft.utils.DBConn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideDAO {

    public static void startRide(Ride ride) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("INSERT INTO rides "
                + "(id, userEmail, driver, source, destination, startTime, duration, distance, cost, driverTime)"
                + "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        st.setString(1, ride.getUser().getEmail());
        st.setInt(2, ride.getDriver().getId());
        st.setString(3, ride.getSource().getName());
        st.setString(4, ride.getDestination().getName());
        st.setTimestamp(5, Timestamp.valueOf(ride.getStartTime()));
        st.setInt(6, ride.getDuration());
        st.setInt(7, ride.getDistance());
        st.setInt(8, ride.getCost());
        st.setInt(9, ride.getDriverTime());
        st.executeUpdate();
        st = conn.prepareStatement("UPDATE drivers SET location = (?),onRoad = (?) WHERE id = (?);");
        st.setString(1, ride.getDestination().getName());
        st.setBoolean(2, true);
        st.setInt(3, ride.getDriver().getId());
        st.executeUpdate();
    }

    public static List<Ride> getUserRides(User user) throws SQLException {
        Connection conn = DBConn.getConn();
        List<Ride> rides = new ArrayList<>();
        PreparedStatement st = conn.prepareStatement("Select * from rides where userEmail = (?);");
        st.setString(1, user.getEmail());
        ResultSet result = st.executeQuery();
        while (result.next()) {
            Ride ride = new Ride(LocationDAO.getLocation(result.getString("source")),
                    LocationDAO.getLocation(result.getString("destination")),
                    DriverDAO.getDriver(result.getInt("driver")), UserDAO.getUser(result.getString("userEmail")),
                    result.getTimestamp("startTime").toLocalDateTime(), result.getInt("distance"),
                    result.getInt("duration"), result.getInt("driverTime"), result.getInt("cost"));
            rides.add(ride);
        }
        DBConn.closeConn();
        return rides;
    }
}
