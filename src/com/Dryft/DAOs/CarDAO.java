package com.Dryft.DAOs;

import com.Dryft.models.Car;
import com.Dryft.utils.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarDAO {
    public static Car getCar(String licenseNumber) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM cars WHERE licenseNumber = (?);");
        st.setString(1, licenseNumber);
        ResultSet result = st.executeQuery();
        DBConn.closeConn();
        if (result.next()) {
            return new Car(result.getString("model"), result.getInt("costPerKm"), result.getString("licenseNumber"), Car.CarType.valueOf(result.getString("CarType")), result.getInt("speed"));
        } else {
            throw new IllegalArgumentException("Car not found");
        }
    }
}
