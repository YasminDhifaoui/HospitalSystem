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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HospitalController implements Initializable {
    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    @FXML
    public Button btn;
    @FXML
    public CheckBox showpassword;
    @FXML
    public Label errorMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Userutil.connectToDB();
        btn.setOnAction(actionEvent -> login());

        showpassword.setOnAction(event -> {
            if (showpassword.isSelected()) {
                password.setStyle("-fx-echo-char: '\\u0000';");  // Unicode for null character to show text
            } else {
                password.setStyle("-fx-echo-char: '•';"); // Bullet character typical for passwords
            }
        });
    }

    public void login() {
        PreparedStatement st = null;
        ResultSet rs = null;
        Connection con = Userutil.connectToDB();
        try {
            st = con.prepareStatement("SELECT * FROM users WHERE USERNAME =? AND PASSWORD = ?");
            st.setString(1, username.getText());
            st.setString(2, password.getText());
            rs = st.executeQuery();
            if (rs.next()) {
                // Login successful, load main page
                loadMainPage();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Login successful!");
                alert.showAndWait();
            } else {
                // Login failed, show error message
                errorMessage.setText("Login Error: Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Consider a more robust error handling depending on your application's needs
        } finally {
            // Ensure resources are freed
            try {
                if (rs != null) rs.close();
                if (st != null) st.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadMainPage() {
        try {
            // Load the root layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Parent root = loader.load();

            // Create the scene with the specified dimensions
            Scene scene = new Scene(root, 848, 550); // Set width to 848 and height to 550

            // Create a new stage and set the scene
            Stage stage = new Stage();
            stage.setTitle("Main Page");
            stage.setScene(scene);
            stage.show();

            // Optionally, close the current (login) window
            Stage currentStage = (Stage) username.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
