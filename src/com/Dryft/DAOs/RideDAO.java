package com.Dryft.DAOs;

import com.Dryft.models.Driver;
import com.Dryft.models.Location;
import com.Dryft.models.Ride;
import com.Dryft.models.User;
import com.Dryft.utils.DBConn;

import java.sql.SQLException;
import java.sql.Timestamp;

public class RideDAO {
    public static void startRide(User user, Driver driver, Location source, Location dest) throws SQLException {
        Ride ride = new Ride(source, dest, driver, user);
        var conn = DBConn.getConn();
        var st = conn.prepareStatement("INSERT INTO rides " +
                "(useremail, driver, source, destination, starttime, duration, distance, cost)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);"
        );
        st.setString(1, user.getEmail());
        st.setInt(2, driver.getId());
        st.setString(3, source.getName());
        st.setString(4, dest.getName());
        st.setTimestamp(5, Timestamp.valueOf(ride.getStartTime()));
        st.setInt(6, ride.getDuration());
        st.setInt(7, ride.getCost());
        st.executeUpdate();
        st = conn.prepareStatement("UPDATE drivers SET location = (?) WHERE id = (?);");
        st.setString(1, dest.getName());
        st.setInt(2, driver.getId());
        st.executeUpdate();
    }
}
