package com.example.hospitalsystem;

import data.Medicine;
import data.Userutil;
import data.patient;
import data.doctor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class PatientController implements Initializable {

    // Patient variables:
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
    private TextField txtCin;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtSearch;

    // Doctor variables:
    @FXML
    private TableView<doctor> doctorTableView;
    @FXML
    private TableColumn<doctor, String> colId;
    @FXML
    private TableColumn<doctor, String> colDoctorFirstName;
    @FXML
    private TableColumn<doctor, String> colDoctorLastName;
    @FXML
    private TableColumn<doctor, String> colDoctorAddress;
    @FXML
    private TableColumn<doctor, String> colDoctorPhone;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtDoctorFirstName;
    @FXML
    private TextField txtDoctorLastName;
    @FXML
    private TextField txtDoctorAddress;
    @FXML
    private TextField txtDoctorPhone;
    @FXML
    private TextField txtDoctorSearch;


    // Medicines variables:
    @FXML
    private TableView<Medicine> medicineTableView;
    @FXML
    private TableColumn<Medicine, String> colMedicineRef;

    @FXML
    private TableColumn<Medicine, String> colMedicineLibelle;
    @FXML
    private TableColumn<Medicine, Double> colMedicinePrice;
    @FXML
    private TextField txtMedicineRef;
    @FXML
    private TextField txtMedicineLibelle;
    @FXML
    private TextField txtMedicinePrice;

    private ObservableList<Medicine> medicineList = FXCollections.observableArrayList();
    private Medicine selectedMedicine = null;

    //Health button
    @FXML
    private ImageView imageView;



    private Connection con = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;
    private ObservableList<patient> patientList = FXCollections.observableArrayList();
    private ObservableList<doctor> doctorList = FXCollections.observableArrayList();
    private patient selectedPatient = null;
    private doctor selectedDoctor = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con = Userutil.connectToDB();
        initializePatientTableColumns();
        loadPatients();
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populatePatientFields(newSelection);
                selectedPatient = newSelection;
            }
        });

        initializeMedicineTableColumns();
        loadMedicines();

        medicineTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateMedicineFields(newSelection);
                selectedMedicine = newSelection;
            }
        });


        // Add logout button event handler
        logoutButton.setOnMouseClicked(event -> handleLogout());

        initializeDoctorTableColumns();
        loadDoctors();
        doctorTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateDoctorFields(newSelection);
                selectedDoctor = newSelection;
            }
        });

        // Set the event handler for the ImageView
        imageView.setOnMouseClicked(event -> redirectToHealthPage());
    }


    //redirect To Health Page
    private void redirectToHealthPage() {
        try {
            // Load the Health FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Health.fxml"));
            Parent healthPage = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) imageView.getScene().getWindow();

            // Set the scene to the health page
            stage.setScene(new Scene(healthPage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------Patient CRUD methods:
    private void initializePatientTableColumns() {
        colCin.setCellValueFactory(cellData -> cellData.getValue().cinProperty());
        colFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
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

    private void populatePatientFields(patient pat) {
        txtCin.setText(pat.getCin());
        txtFirstName.setText(pat.getFirstName());
        txtLastName.setText(pat.getLastName());
        txtAddress.setText(pat.getAddress());
        txtPhone.setText(pat.getPhone());
    }

    // Helper method to check if the cin and phone strings contains only numbers
    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    @FXML
    private void addPatient(MouseEvent event) {
        String cin = txtCin.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();

        if (!isNumeric(cin) || !isNumeric(phone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("CIN and Phone Number should contain only numbers.");
            alert.showAndWait();
            return;
        }

        try {
            st = con.prepareStatement("INSERT INTO patients (cin, firstName, lastName, adresse, phone) VALUES (?, ?, ?, ?, ?)");
            st.setString(1, cin);
            st.setString(2, firstName);
            st.setString(3, lastName);
            st.setString(4, address);
            st.setString(5, phone);
            st.executeUpdate();

            patient newPatient = new patient(cin, firstName, lastName, address, phone);
            tableView.getItems().add(newPatient);
            clearPatientFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updatePatient(MouseEvent event) {
        if (selectedPatient != null) {
            String cin = txtCin.getText();
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String address = txtAddress.getText();
            String phone = txtPhone.getText();

            if (!isNumeric(phone)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Phone Number should contain only numbers.");
                alert.showAndWait();
                return;
            }

            try {
                st = con.prepareStatement("UPDATE patients SET firstName = ?, lastName = ?, adresse = ?, phone = ? WHERE cin = ?");
                st.setString(1, firstName);
                st.setString(2, lastName);
                st.setString(3, address);
                st.setString(4, phone);
                st.setString(5, cin);
                st.executeUpdate();

                selectedPatient.setFirstName(firstName);
                selectedPatient.setLastName(lastName);
                selectedPatient.setAddress(address);
                selectedPatient.setPhone(phone);
                tableView.refresh();

                clearPatientFields();
                selectedPatient = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deletePatient(MouseEvent event) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            System.out.println("No patient selected for deletion.");
            return;
        }

        patient selectedPatient = tableView.getItems().get(selectedIndex);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the patient with CIN: " + selectedPatient.getCin() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                st = con.prepareStatement("DELETE FROM patients WHERE cin = ?");
                st.setString(1, selectedPatient.getCin());
                st.executeUpdate();

                tableView.getItems().remove(selectedIndex);
                clearPatientFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void searchPatients() {
        String searchText = txtSearch.getText().toLowerCase();
        ObservableList<patient> filteredList = FXCollections.observableArrayList();

        for (patient pat : patientList) {
            if (pat.getFirstName().toLowerCase().contains(searchText) || pat.getLastName().toLowerCase().contains(searchText) ||
                    pat.getAddress().toLowerCase().contains(searchText) || pat.getPhone().toLowerCase().contains(searchText) ||
                    pat.getCin().toLowerCase().contains(searchText)) {
                filteredList.add(pat);
            }
        }
        tableView.setItems(filteredList);
    }

    private void clearPatientFields() {
        txtCin.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtAddress.clear();
        txtPhone.clear();
    }

    //--------------------------------------------------- Doctor CRUD methods:


    private void initializeDoctorTableColumns() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colDoctorFirstName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        colDoctorLastName.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        colDoctorAddress.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        colDoctorPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
    }


    private void loadDoctors() {
        doctorList.clear();
        try {
            st = con.prepareStatement("SELECT ID, DFirstName, DLastName, DAdresse, DPhone FROM doctors");
            rs = st.executeQuery();
            while (rs.next()) {
                doctorList.add(new doctor(rs.getString("ID"), rs.getString("DFirstName"), rs.getString("DLastName"), rs.getString("DAdresse"), rs.getString("DPhone")));
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        doctorTableView.setItems(doctorList);
    }


    private void populateDoctorFields(doctor doc) {
        txtId.setText(doc.getId());
        txtDoctorFirstName.setText(doc.getFirstName());
        txtDoctorLastName.setText(doc.getLastName());
        txtDoctorAddress.setText(doc.getAddress());
        txtDoctorPhone.setText(doc.getPhone());
    }

    @FXML
    private void addDoctor(MouseEvent event) {
        String id = txtId.getText();
        String firstName = txtDoctorFirstName.getText();
        String lastName = txtDoctorLastName.getText();
        String address = txtDoctorAddress.getText();
        String phone = txtDoctorPhone.getText();

        if (!isNumeric(id) || !isNumeric(phone)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("ID and Phone Number should contain only numbers.");
            alert.showAndWait();
            return;
        }

        try {
            st = con.prepareStatement("INSERT INTO doctors (ID,DFirstName, DLastName, DAdresse, DPhone) VALUES (?, ?, ?, ?, ?)");
            st.setString(1, id);
            st.setString(2, firstName);
            st.setString(3, lastName);
            st.setString(4, address);
            st.setString(5, phone);
            st.executeUpdate();

            doctor newDoctor = new doctor(id, firstName, lastName, address, phone);
            doctorTableView.getItems().add(newDoctor);
            clearDoctorFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateDoctor(MouseEvent event) {
        if (selectedDoctor != null) {
            String id = txtId.getText();
            String firstName = txtDoctorFirstName.getText();
            String lastName = txtDoctorLastName.getText();
            String address = txtDoctorAddress.getText();
            String phone = txtDoctorPhone.getText();

            if (!isNumeric(phone)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Phone Number should contain only numbers.");
                alert.showAndWait();
                return;
            }

            try {
                st = con.prepareStatement("UPDATE doctors SET DFirstName = ?, DLastName = ?, DAdresse = ?, DPhone = ? WHERE id = ?");
                st.setString(1, firstName);
                st.setString(2, lastName);
                st.setString(3, address);
                st.setString(4, phone);
                st.setString(5, id);
                st.executeUpdate();

                selectedDoctor.setFirstName(firstName);
                selectedDoctor.setLastName(lastName);
                selectedDoctor.setAddress(address);
                selectedDoctor.setPhone(phone);
                doctorTableView.refresh();

                clearDoctorFields();
                selectedDoctor = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteDoctor(MouseEvent event) {
        int selectedIndex = doctorTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            System.out.println("No doctor selected for deletion.");
            return;
        }

        doctor selectedDoctor = doctorTableView.getItems().get(selectedIndex);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the doctor with ID: " + selectedDoctor.getId() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                st = con.prepareStatement("DELETE FROM doctors WHERE id = ?");
                st.setString(1, selectedDoctor.getId());
                st.executeUpdate();

                doctorTableView.getItems().remove(selectedIndex);
                clearDoctorFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void searchDoctors() {
        String searchText = txtDoctorSearch.getText().toLowerCase();
        ObservableList<doctor> filteredList = FXCollections.observableArrayList();

        for (doctor doc : doctorList) {
            if (doc.getFirstName().toLowerCase().contains(searchText) || doc.getLastName().toLowerCase().contains(searchText) ||
                    doc.getAddress().toLowerCase().contains(searchText) || doc.getPhone().toLowerCase().contains(searchText) ||
                    doc.getId().toLowerCase().contains(searchText)) {
                filteredList.add(doc);
            }
        }
        doctorTableView.setItems(filteredList);
    }

    private void clearDoctorFields() {
        txtId.clear();
        txtDoctorFirstName.clear();
        txtDoctorLastName.clear();
        txtDoctorAddress.clear();
        txtDoctorPhone.clear();
    }

    private void clearFields() {
        clearPatientFields();
        clearDoctorFields();
    }

    private Stage stage;
    @FXML
    private void printPatientInfo(MouseEvent event) {
        String cin = txtCin.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String address = txtAddress.getText();
        String phone = txtPhone.getText();

        String patientInfo = "Patient Information:\n" +
                "CIN: " + cin + "\n" +
                "First Name: " + firstName + "\n" +
                "Last Name: " + lastName + "\n" +
                "Address: " + address + "\n" +
                "Phone Number: " + phone;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Patient Information");
        fileChooser.setInitialFileName("patient_information.pdf");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(patientInfo.getBytes());
                System.out.println("Patient information saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void printDoctorInfo(MouseEvent event) {
        String id = txtId.getText();
        String firstName = txtDoctorFirstName.getText();
        String lastName = txtDoctorLastName.getText();
        String address = txtDoctorAddress.getText();
        String phone = txtDoctorPhone.getText();

        String doctorInfo = "Doctor Information:\n" +
                "ID: " + id + "\n" +
                "First Name: " + firstName + "\n" +
                "Last Name: " + lastName + "\n" +
                "Address: " + address + "\n" +
                "Phone Number: " + phone;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Doctor Information");
        fileChooser.setInitialFileName("doctor_information.pdf");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(doctorInfo.getBytes());
                System.out.println("Doctor information saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Logout :

    @FXML
    private ImageView logoutButton;
    private void handleLogout() {
        // Display confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Logout Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to logout?");

        // Show the confirmation dialog and wait for user input
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // If user clicks "OK", proceed with logout
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            // Display logout message
//            Alert logoutAlert = new Alert(Alert.AlertType.INFORMATION);
//            logoutAlert.setTitle("Logout");
//            logoutAlert.setHeaderText(null);
//            logoutAlert.setContentText("You have been logged out.");
//            logoutAlert.showAndWait();

            // Redirect to login page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


    // -------------------------------------------------------------------medicines CRUD methods:


    private void initializeMedicineTableColumns() {
        colMedicineRef.setCellValueFactory(cellData -> cellData.getValue().refProperty());
        colMedicineLibelle.setCellValueFactory(cellData -> cellData.getValue().libelleProperty());
        colMedicinePrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
    }
// Add CRUD methods for medicines:

    // Load medicines from the database and populate the medicineList
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

    private void populateMedicineFields(Medicine med) {
        txtMedicineRef.setText(med.getRef());
        txtMedicineLibelle.setText(med.getLibelle());
        txtMedicinePrice.setText(String.valueOf(med.getPrice()));
    }


    // Method to add a new medicine


    @FXML
    private void addMedicine(MouseEvent event) {
        String ref = txtMedicineRef.getText();
        String lib = txtMedicineLibelle.getText();
        String priceText = txtMedicinePrice.getText();

        if (!isAlphanumeric(ref) || !isNumeric(priceText)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Ref should contain letters and numbers, and Price should contain only numbers.");
            alert.showAndWait();
            return;
        }

        double price = Double.parseDouble(priceText);

        try {
            st = con.prepareStatement("INSERT INTO medicines (Ref, Libelle, Price) VALUES (?, ?, ?)");

            st.setString(1, ref);
            st.setString(2, lib);
            st.setDouble(3, price);

            // Execute the insert statement
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                // If the insert was successful, create a new Medicine object and add it to the table view
                Medicine newMed = new Medicine(ref, lib, price);
                medicineTableView.getItems().add(newMed);
                clearMedicineFields();
            } else {
                // Handle the case where the insert failed
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to add medicine to the database.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Method to update an existing medicine
    @FXML
    private void updateMedicine() {
        if (selectedMedicine != null) {
            String ref = txtMedicineRef.getText();
            String libelle = txtMedicineLibelle.getText();
            double price = Double.parseDouble(txtMedicinePrice.getText());

            try {
                st = con.prepareStatement("UPDATE medicines SET Libelle = ?, Price = ? WHERE Ref = ?");
                st.setString(1, libelle);
                st.setDouble(2, price);
                st.setString(3, ref);
                st.executeUpdate();

                selectedMedicine.setLibelle(libelle);
                selectedMedicine.setPrice(price);
                medicineTableView.refresh();

                clearMedicineFields();
                selectedMedicine = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to delete a medicine
    @FXML
    private void deleteMedicine() {
        int selectedIndex = medicineTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            System.out.println("No medicine selected for deletion.");
            return;
        }

        Medicine selectedMedicine = medicineTableView.getItems().get(selectedIndex);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the medicine with Ref: " + selectedMedicine.getRef() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                st = con.prepareStatement("DELETE FROM medicines WHERE Ref = ?");
                st.setString(1, selectedMedicine.getRef());
                st.executeUpdate();

                medicineList.remove(selectedIndex);
                clearMedicineFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Clear medicine input fields
    private void clearMedicineFields() {
        txtMedicineRef.clear();
        txtMedicineLibelle.clear();
        txtMedicinePrice.clear();
    }

    @FXML
    private TextField txtMedicineSearch;

    @FXML
    private void searchMedicines() {
        String searchText = txtMedicineSearch.getText().toLowerCase();
        ObservableList<Medicine> filteredList = FXCollections.observableArrayList();

        for (Medicine med : medicineList) {
            if (med.getRef().toLowerCase().contains(searchText) ||
                    med.getLibelle().toLowerCase().contains(searchText) ||
                    String.valueOf(med.getPrice()).toLowerCase().contains(searchText)) {
                filteredList.add(med);
            }
        }
        medicineTableView.setItems(filteredList);
    }



}
