package com.Dryft.models;

import com.Dryft.DAOs.UserDAO;
import com.Dryft.DAOs.RideDAO;

import java.sql.SQLException;

class User {
    private final String fullname;
    private final String email;
    private final char sex;
    private String password;
    private int balance;

    public User(String fullname, String email, String password, char sex, int balance) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.sex = sex;
        this.balance = balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public char getSex() {
        return sex;
    }

    public int getBalance() {
        return balance;
    }

    public void incrementBalance(int increment)throws SQLException {
        balance += increment;
        UserDAO.incrementBalance(email, balance);
    }

    public Ride bookRide(Driver driver,Location source,Location destination)throws SQLException {
        Ride ride=new Ride(source,destination,driver,this);
        RideDAO.bookRide(ride);
        return ride;
    }
}
