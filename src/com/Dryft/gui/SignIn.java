package com.Dryft.gui;

import com.Dryft.DAOs.UserDAO;
import com.Dryft.exceptions.UserSideException;
import com.Dryft.models.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignIn extends JFrame {

    void quit() {
        this.dispose();
    }

    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;

    /**
     * Create the frame.
     */
    public SignIn() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 581, 436);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblDryft = new JLabel("WELCOME TO DRYFT!");
        lblDryft.setForeground(new Color(0, 0, 0));
        lblDryft.setBackground(Color.ORANGE);
        lblDryft.setFont(new Font("Century", Font.PLAIN, 20));
        lblDryft.setBounds(170, 11, 261, 28);
        contentPane.add(lblDryft);

        JLabel lblUsername = new JLabel("USERNAME");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblUsername.setBounds(105, 114, 130, 14);
        contentPane.add(lblUsername);

        JLabel lblPassword = new JLabel("PASSWORD");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPassword.setBounds(105, 168, 130, 20);
        contentPane.add(lblPassword);

        textField = new JTextField();
        textField.setBounds(245, 111, 157, 20);
        contentPane.add(textField);
        textField.setColumns(10);

        JButton btnSignIn = new JButton("SIGN IN");
        btnSignIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnSignIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();
                String password = String.valueOf(passwordField.getPassword());
                if (username.length() == 0 || password.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Please enter your credentials.");
                    return;
                }
                String message = null;
                User user = null;
                try {
                    user = UserDAO.retrieveUserDetails(username, password);
                } catch (SQLException ex) {
                    message = "Sorry but the program has crashed.";
                    JOptionPane.showMessageDialog(null, message);
                    Logger.getLogger(SignIn.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);
                } catch (UserSideException ex) {
                    switch (ex.getErrorCode()) {
                        case UserNotFound:
                            message = "Your username is invalid. Please check it.";
                            break;
                        case InvalidCredentials:
                            message = "Please check your login credentials and try again.";
                            break;
                        default:
                            message = "There is some error. Please reload the application.";
                    }
                }
                if (message == null) {
                    new Home(user).setVisible(true);
                    quit();
                } else {
                    JOptionPane.showMessageDialog(null, message);
                }
            }
        });
        btnSignIn.setBounds(188, 232, 89, 23);
        contentPane.add(btnSignIn);

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnCancel.setForeground(Color.RED);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnCancel.setBounds(342, 232, 89, 23);
        contentPane.add(btnCancel);

        JButton btnNewButton = new JButton("SIGN UP");
        btnNewButton.setToolTipText("Click to Sign up!");
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnNewButton.setForeground(Color.BLUE);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SignUp().setVisible(true);
            }
        });
        btnNewButton.setBounds(450, 336, 89, 23);
        contentPane.add(btnNewButton);

        JLabel lblNotRegisteredYet = new JLabel("New to Dryft ? Sign up!");
        lblNotRegisteredYet.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNotRegisteredYet.setBounds(267, 336, 185, 18);
        contentPane.add(lblNotRegisteredYet);

        passwordField = new JPasswordField();
        passwordField.setBounds(245, 168, 157, 20);
        contentPane.add(passwordField);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SignIn frame = new SignIn();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
