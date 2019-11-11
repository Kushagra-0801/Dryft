package com.Dryft.DAOs;

import com.Dryft.models.Location;
import com.Dryft.utils.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {
    public static Location getLocation(String name) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM locations WHERE name = (?);");
        st.setString(1, name);
        ResultSet result = st.executeQuery();
        DBConn.closeConn();
        if (result.next()) {
            return new Location(result.getString("name"), result.getInt("x"), result.getInt("y"));
        } else {
            throw new IllegalArgumentException("Location not found");
        }
    }

    public static List<Location> getAllLocations() throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM locations;");
        ResultSet result = st.executeQuery();
        List<Location> locations = new ArrayList<>();
        while (result.next()) {
            Location temp = new Location(result.getString("name"), result.getInt("x"), result.getInt("y"));
            locations.add(temp);
        }
        DBConn.closeConn();
        return locations;
    }
}
