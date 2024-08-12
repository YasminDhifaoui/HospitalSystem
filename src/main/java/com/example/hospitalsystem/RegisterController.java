package com.example.hospitalsystem;

import data.Userutil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button btnRegister;
    @FXML
    private CheckBox showpassword;
    @FXML
    private Label errorMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Userutil.connectToDB();
        btnRegister.setOnAction(this::handleRegisterClick);

        showpassword.setOnAction(event -> {
            if (showpassword.isSelected()) {
                password.setStyle("-fx-echo-char: '\\u0000';");  // Unicode for null character to show text
            } else {
                password.setStyle("-fx-echo-char: '•';"); // Bullet character typical for passwords
            }
        });
    }
    @FXML
    private void handleRegisterClick(ActionEvent event) {
        String enteredUsername = username.getText().trim();
        String enteredPassword = password.getText().trim();

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Fields");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both username and password.");
            alert.showAndWait();
            return;
        }

        try {
            Connection con = Userutil.connectToDB();

            // Check if the username already exists
            PreparedStatement checkUserStmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE USERNAME = ?");
            checkUserStmt.setString(1, enteredUsername);
            ResultSet rs = checkUserStmt.executeQuery();
            rs.next();
            int userCount = rs.getInt(1);

            if (userCount > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Username Taken");
                alert.setHeaderText(null);
                alert.setContentText("Username already exists. Please choose a different username.");
                alert.showAndWait();
            } else {
                // Insert new user if username does not exist
                PreparedStatement st = con.prepareStatement("INSERT INTO users (USERNAME, PASSWORD) VALUES (?, ?)");
                st.setString(1, enteredUsername);
                st.setString(2, enteredPassword);
                int rowsAffected = st.executeUpdate();

                if (rowsAffected > 0) {
                    loadMainPageWithSuccess();
                } else {
                    errorMessage.setText("Registration failed. Please try again.");
                }

                st.close();
            }

            checkUserStmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage.setText("Error occurred. Please try again.");
        }
    }

    private void loadMainPageWithSuccess() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 848, 550);

            Stage stage = new Stage();
            stage.setTitle("Main Page");
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) username.getScene().getWindow();
            currentStage.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully!");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void redirectToLoginPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
