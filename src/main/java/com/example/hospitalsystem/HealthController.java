package com.example.hospitalsystem;

import data.Medicine;
import data.PatMed;
import data.Userutil;
import data.patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class HealthController implements Initializable {

    @FXML
    private TableView<patient> tableView;
    @FXML
    private TableColumn<patient, String> colCin;
    @FXML
    private TableColumn<patient, String> colFirstName;
    @FXML
    private TableColumn<patient, String> colLastName;
    @FXML
    private TableColumn<patient, String> colAddress;
    @FXML
    private TableColumn<patient, String> colPhone;

    @FXML
    private TableView<Medicine> medicineTableView;
    @FXML
    private TableColumn<Medicine, String> colMedicineRef;
    @FXML
    private TableColumn<Medicine, String> colMedicineLibelle;
    @FXML
    private TableColumn<Medicine, Double> colMedicinePrice;

    @FXML
    private ImageView imageView;

    @FXML
    private TableView<PatMed> patMedTableView;


    private Connection con;
    private PreparedStatement st;
    private ResultSet rs;

    private ObservableList<patient> patientList = FXCollections.observableArrayList();
    private ObservableList<Medicine> medicineList = FXCollections.observableArrayList();

    private ObservableList<PatMed> patMedList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con = Userutil.connectToDB();

        initializePatientTableColumns();
        initializeMedicineTableColumns();

        loadPatients();
        loadMedicines();
        loadPatMed();

        // Initialize patMedTableView columns
        TableColumn<PatMed, String> colPatientCIN = new TableColumn<>("Patient CIN");
        colPatientCIN.setCellValueFactory(new PropertyValueFactory<>("patientCIN"));

        TableColumn<PatMed, String> colMedRef = new TableColumn<>("Med Ref");
        colMedRef.setCellValueFactory(new PropertyValueFactory<>("medRef"));

        TableColumn<PatMed, String> colDateAffectation = new TableColumn<>("Date Affectation");
        colDateAffectation.setCellValueFactory(new PropertyValueFactory<>("dateAffectation"));

        patMedTableView.getColumns().addAll(colPatientCIN, colMedRef, colDateAffectation);


        // Set double-click event listener to remove selected item
        patMedTableView.setRowFactory(tv -> {
            TableRow<PatMed> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    patMedTableView.getItems().remove(row.getItem());
                }
            });
            return row;
        });

        // Load data from the database
        loadDataFromDatabase();

    }


    private void loadDataFromDatabase() {
        // Connect to the database
        try (Connection connection = Userutil.connectToDB()) {
            // Prepare SQL statement to select data from patMed table
            String sql = "SELECT * FROM patMed";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Execute query and process result set
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<PatMed> patMedList = new ArrayList<>();
                    while (resultSet.next()) {
                        // Create PatMed objects from result set and add to the list
                        PatMed patMed = new PatMed(resultSet.getString("patientCIN"),
                                resultSet.getString("medRef"),
                                resultSet.getString("dateAffectation"));
                        patMedList.add(patMed);
                    }
                    // Populate TableView with data
                    patMedTableView.getItems().addAll(patMedList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database connection or query errors
        }
    }


@FXML

    private void handlePlusIconClick(MouseEvent event) {
        // Get selected patient and medicine data
        patient selectedPatient = tableView.getSelectionModel().getSelectedItem();
        Medicine selectedMedicine = medicineTableView.getSelectionModel().getSelectedItem();

        // Insert into database (pseudo code)
        if (selectedPatient != null && selectedMedicine != null) {
            // Insert into patMed table
            insertIntoPatMed(selectedPatient.getCin(), selectedMedicine.getRef(), LocalDate.now().toString());

            // Add to TableView
            patMedTableView.getItems().add(new PatMed(selectedPatient.getCin(), selectedMedicine.getRef(), LocalDate.now().toString()));
        }
    }


    // Method to insert data into patMed table
    private void insertIntoPatMed(String patientCIN, String medRef, String dateAffectation) {
        try (Connection connection = Userutil.connectToDB()) {
            // Prepare SQL statement to insert data into patMed table
            String sql = "INSERT INTO patMed (patient_CIN, med_ref, date_Affectation) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, patientCIN);
                statement.setString(2, medRef);
                statement.setString(3, dateAffectation); // Assuming dateAffectation is of type VARCHAR in the database

                // Execute the insert statement
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Record inserted successfully.");
                } else {
                    System.out.println("No rows affected.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to insert into database.");
        }
    }


    @FXML
    private void deletePatMed(MouseEvent event) {
        // Your code to handle the deletion
        PatMed selectedItem = patMedTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            patMedTableView.getItems().remove(selectedItem);
            // Also, delete from the database
            deleteFromPatMed(selectedItem);
        }
    }

    private void deleteFromPatMed(PatMed patMed) {
        try (Connection connection = Userutil.connectToDB()) {
            String sql = "DELETE FROM patMed WHERE patient_cin = ? AND med_ref = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, patMed.getPatientCIN());
                statement.setString(2, patMed.getMedRef());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Record deleted successfully.");
                } else {
                    System.out.println("No rows affected.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete from database.");
        }
    }




    private void initializePatientTableColumns() {
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void initializeMedicineTableColumns() {
        colMedicineRef.setCellValueFactory(new PropertyValueFactory<>("ref"));
        colMedicineLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        colMedicinePrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadPatients() {
        patientList.clear();
        try {
            st = con.prepareStatement("SELECT * FROM patients");
            rs = st.executeQuery();
            while (rs.next()) {
                patientList.add(new patient(rs.getString("cin"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("adresse"), rs.getString("phone")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(patientList);
    }

    private void loadMedicines() {
        medicineList.clear();
        try {
            st = con.prepareStatement("SELECT * FROM medicines");
            rs = st.executeQuery();
            while (rs.next()) {
                medicineList.add(new Medicine(rs.getString("Ref"), rs.getString("Libelle"), rs.getDouble("Price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        medicineTableView.setItems(medicineList);
    }

    private void loadPatMed() {
        patMedList.clear();
        try {
            st = con.prepareStatement("SELECT * FROM patMed");
            rs = st.executeQuery();
            while (rs.next()) {
                patMedList.add(new PatMed(rs.getString("patient_cin"), rs.getString("med_ref"), rs.getString("date_affectation")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        patMedTableView.setItems(patMedList);
    }

    @FXML
    private void redirectToMainPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
